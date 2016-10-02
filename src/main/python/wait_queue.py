"""Wait queue.

Use case: design a service that pairs up users for a upcoming game.
As a client, request is sent to server with user ID and game setting. If
pair-up is successful, the API should return your foe's ID and game settings.
If pair-up fails due to completely idle server, raise exception.
"""

import threading
import time
import unittest

_NOT_MATCHED_YET = None
_MAX_RETRIES = 3
_SLEEP = 5  # Make sure retries do not cause HTTP request timeout


def _search_retry(func):
    """Decorates _active_search_for_foe method for multiple retries."""
    def inner(*args, **kwargs):
        """Inner function."""
        attempts = 0
        while attempts < _MAX_RETRIES:
            try:
                return func(*args, **kwargs)
            except FoeNotFoundException:
                time.sleep(_SLEEP)
                attempts += 1
        message = 'Foe not found after %d retries' % (attempts, )
        raise FoeNotFoundException(message)
    return inner


class WaitQueue(object):

    """WaitQueue class."""

    _LOCK = threading.Lock()
    _REGISTER = {}
    _SETTINGS = {}

    @classmethod
    def search_foe(clz, user_id, settings):
        """Searches potential foe for a match.

        Multiple searches will be conducted until max retries are exhausted and
        til then exception will be raised..

        :param user_id: str, user ID that requests the search
        :param settings: dict, game setting that will be picked up by foe if
                         match is paired successfully
        :param return: tuple, foe ID and foe settings
        :raise FoeNotFoundException: when max retries are exhausted
        """
        if user_id in clz._REGISTER:
            message = 'User %s already being processed' % (user_id,)
            raise UserAlreadyInProcessException(message)

        clz._register(user_id, settings)

        # Another thread might have slipped in and actively marked this user
        # as foe.
        if clz._REGISTER[user_id] == _NOT_MATCHED_YET:
            try:
                clz._active_search_for_foe(user_id)
            except FoeNotFoundException:
                # Unregister self due to matching failure.
                clz._unregister(user_id)
                raise
        foe_id = clz._REGISTER[user_id]
        foe_setting = clz._unregister(foe_id)
        return foe_id, foe_setting

    @classmethod
    def _unregister(clz, user_id):
        """Unregisters a user and associated settings."""
        clz._REGISTER.pop(user_id)
        return clz._SETTINGS.pop(user_id)

    @classmethod
    def _register(clz, user_id, settings):
        """Registers a user and associated settings."""
        clz._REGISTER[user_id] = _NOT_MATCHED_YET
        clz._SETTINGS[user_id] = settings

    @classmethod
    def _pair(clz, user_id1, user_id2):
        """Pairs up two users by their IDs."""
        clz._REGISTER[user_id1] = user_id2
        clz._REGISTER[user_id2] = user_id1

    @classmethod
    @_search_retry
    def _active_search_for_foe(clz, user_id):
        """"""
        for registered_id, settings in clz._REGISTER.items():
            if registered_id != user_id and settings == _NOT_MATCHED_YET:
                # Skip self and other users has been paired in a match.
                with clz._LOCK:
                    # Double enter-lock condition check.
                    if settings == _NOT_MATCHED_YET:
                        clz._pair(user_id, registered_id)
                        return
        raise FoeNotFoundException('Foe not found')


class UserAlreadyInProcessException(Exception):

    def __init__(self, message, errors):
        """Initialize."""
        super(UserAlreadyInProcessException, self).__init__(message)
        self.errors = errors;


class FoeNotFoundException(Exception):

    pass


class WaitingQueueTestSuite(unittest.TestCase):

    """WaitQueue class test suite."""

    def setUp(self):
        """Sets up test suite."""
        self.user1_id = 'user1_id'
        self.user1_setting = 'user1_setting'
        self.user2_id = 'user2_id'
        self.user2_setting = 'user2_setting'

    def tearDown(self):
        """Tears down test suite."""
        self.assertEquals(0, len(WaitQueue._SETTINGS))
        self.assertEquals(0, len(WaitQueue._REGISTER))

    def test_find_match_with_manually_put_user(self):
        """"""
        WaitQueue._register(self.user2_id, self.user2_setting)

        matched_user_id, matched_user_setting = WaitQueue.search_foe(
                self.user1_id, self.user1_setting)
        self.assertEquals(self.user2_id, matched_user_id)
        self.assertEquals(self.user2_setting, self.user2_setting)

        # user2 is manually registered so needs to be cleared manually as well.
        self.assertEquals(1, len(WaitQueue._SETTINGS))
        self.assertEquals(1, len(WaitQueue._REGISTER))

        WaitQueue._SETTINGS.clear()
        WaitQueue._REGISTER.clear()

    def test_raise_when_empty(self):
        """"""
        global _SLEEP
        _SLEEP = 0
        with self.assertRaises(FoeNotFoundException):
            WaitQueue.search_foe(self.user1_id, self.user1_setting)


if __name__ == '__main__':
    unittest.main()
