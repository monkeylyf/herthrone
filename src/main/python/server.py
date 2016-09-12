#!/usr/bin/env python3.5

"""Flask server for UI."""

import ast
import json
import os
import time

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
    class_type = payload.get('class_type')
    cards = {}
    if class_type:
        response = client.list_cards(class_type=class_type)
        for minion in response.minions:
            cards[minion.name] = client.proto_to_json(minion)
        for spell in response.spells:
            cards[spell.name] = client.proto_to_json(spell)
        for weapon in response.weapons:
            cards[weapon.name] = client.proto_to_json(weapon)
    return Response(
        json.dumps(cards),
        mimetype='application/json',
        headers={
            'Cache-Control': 'no-cache',
            'Access-Control-Allow-Origin': '*'
        }
    )


if __name__ == '__main__':
    app.run(port=3000)
