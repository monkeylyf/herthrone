"""Herthrone grpc Python client."""

import functools
import grpc
import json
from google.protobuf import json_format

import herthrone_pb2


def list_heroes():
    """Gets all heroes."""
    stub = _get_stub()
    request = herthrone_pb2.ListHeroesRequest(name='you')
    return stub.ListHeroes(request)


def list_cards(class_type):
    """Gets all cards by a class type."""
    stub = _get_stub()
    request = herthrone_pb2.ListRequest(class_type=class_type)
    return stub.ListCards(request)


def list_minions(class_type):
    """Gets all minions by a class type."""
    stub = _get_stub()
    request = herthrone_pb2.ListRequest(class_type=class_type)
    return stub.ListMinions(request)


def list_spells(class_type):
    """Gets all spells by a class type."""
    stub = _get_stub()
    request = herthrone_pb2.ListRequest(class_type=class_type)
    return stub.ListSpells(request)


def list_weapons(class_type):
    """Gets all weapons by a class type."""
    stub = _get_stub()
    request = herthrone_pb2.ListRequest(class_type=class_type)
    return stub.ListWeapons(request)


def start_game(game_settings):
    """Requests server to start a game and returns game ID."""
    stub = _get_stub()
    request = herthrone_pb2.StartGameRequest(game_settings=game_settings)
    return stub.StartGame(request)


def get_game_view(game_id, user_id):
    """Gets the metadata of an existing game."""
    stub = _get_stub()
    request = herthrone_pb2.GetGameViewRequest(game_id=game_id, user_id=user_id)
    return stub.GetGameView(request)


@functools.lru_cache()
def _get_channel():
    """Gets default grpc channel."""
    return grpc.insecure_channel('localhost:50051')


@functools.lru_cache()
def _get_stub(channel=None):
    """Gets Herthrone stub."""
    channel = channel or _get_channel()
    return herthrone_pb2.HerthroneStub(channel)


def proto_to_json(proto, **kwargs):
    """Converts a protobuf object into json."""
    json_str = json_format.MessageToJson(proto)
    json_data = json.loads(json_str)
    json_data.update(kwargs)
    return json_data
