#!/usr/bin/env python3.5

"""Flask server for UI."""

import json
from flask import Flask, Response, request

import client

#TODO: fixit.
DEV_INDEX_FILE_PATH = '/Users/yifengliu/google/ui_herthrone/src'

app = Flask(__name__, static_url_path='', static_folder=DEV_INDEX_FILE_PATH)
app.add_url_rule('/', 'root', lambda: app.send_static_file('index.html'))


@app.route('/api/heroes', methods=['GET'])
def heroes():
    """"""
    response = client.list_heroes()
    heroes = []
    for hero in response.heroes:
        hero_json = {
            'name': hero.name,
            'display_name': hero.display_name,
            'class_type': hero.class_type,
            'hero_power': hero.hero_power,
            'description': hero.description
        }
        heroes.append(hero_json)
    return Response(
        json.dumps(heroes),
        mimetype='application/json',
        headers={
            'Cache-Control': 'no-cache',
            'Access-Control-Allow-Origin': '*'
        }
    )

@app.route('/api/cards', methods=['POST'])
def cards():
    """"""
    payload = request.get_json(force=True)
    class_type = payload.get('classType')
    cards = {}
    if class_type:
        response = client.list_cards(class_type=class_type)
        for minion in response.minions:
            cards[minion.name] = client.proto_to_json(minion, type='minion')
        for spell in response.spells:
            cards[spell.name] = client.proto_to_json(spell, type='spell')
        for weapon in response.weapons:
            cards[weapon.name] = client.proto_to_json(weapon, type='weapon')
    return Response(
        json.dumps(cards),
        mimetype='application/json',
        headers={
            'Cache-Control': 'no-cache',
            'Access-Control-Allow-Origin': '*'
        }
    )

@app.route('/api/game/start', methods=['POST'])
def start_game():
    """"""
    payload = request.get_json(force=True)
    hero = payload['hero']
    deck = payload['deck']
    assert(sum(deck.values()) == 30)

    deck_list = []
    for card_name, count in deck.items():
        deck_list += [card_name] * count

    game_settings = {
        'hero': hero,
        'cards': deck_list,
        'player_number': 2  # TODO: Hard code for now.
    }

    # TODO: Should be getting another player's info from queue.
    #foe_game_setting = WaitQueue.search_foe('fake_user_id', game_settings)
    #game_settings = [game_settings] * 2

    #response = client.start_game(game_settings)
    response = {
        'game_id': 111111,
        'feo': {
            'hero': hero
        },
        'own': {
            'hero': hero,
            'cards': deck_list
        }
    }
    return Response(
        json.dumps(response),
        mimetype='application/json',
        headers={
            'Cache-Control': 'no-cache',
            'Access-Control-Allow-Origin': '*'
        }
    )


@app.route('/api/game/view', methods=['POST'])
def get_game_view():
    """"""
    payload = request.get_json(force=True)
    game_id = payload['gameId']
    user_id = payload['userId']
    #response = client.get_game_view(game_id, user_id)
    #response = client.proto_to_json(response)
    yeti = {
        'name': 'CHILLWIND_YETI',
        'display_name': 'Chillwind Yeti',
        'health': 5,
        'max_health': 5,
        'attack': 4,
        'crystal': 4
    }
    response = {
        'own': {
          'hero': {
            'name': 'Malfurion Stormrage',
            'health': '30',
            'attack': 0,
            'armor': '0'
           },
          'hero_power': 'hero_power1',
          'board': [yeti] * 2,
          'hand': [yeti] * 4,
          'secret': [],
          'crystal': 2,
          'max_crystal': 2,
          'deck': 30
        },
        'foe': {
          'hero': {
            'name': 'Jaina Proudmoore',
            'health': '30',
            'attack': 1,
            'armor': '0'
           },
          'hero_power': 'hero_power2',
          'board': [yeti] * 3,
          'hand': [{}] * 5,
          'secret': 0,
          'crystal': 2,
          'max_crystal': 2,
          'deck': 30
        }
    }
    return Response(
        json.dumps(response),
        mimetype='application/json',
        headers={
            'Cache-Control': 'no-cache',
            'Access-Control-Allow-Origin': '*'
        }
    )


if __name__ == '__main__':
    app.run(port=3000)
