"""Herthrone grpc Python client."""
import herthrone_pb2

import grpc


def list_heroes():
    """Gets all heroes."""
    channel = _get_channel()
    stub = herthrone_pb2.HerthroneStub(channel)
    request = herthrone_pb2.ListHeroesRequest(name='you')
    return stub.ListHeroes(request)

def list_cards(class_type):
    """Gets all cards by a hero."""
    channel = _get_channel()
    stub = herthrone_pb2.HerthroneStub(channel)
    request = herthrone_pb2.ListCardsRequest(class_type=class_type)
    return stub.ListCards(request)


def _get_channel():
    """"""
    return grpc.insecure_channel('localhost:50051')
