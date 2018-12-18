#!/usr/bin/env bash
set -e

echo "==== Building the bialydunajec-commons library"
ng build bialydunajec-commons

echo "==== Building the bialydunajec-frontend-main application - dev mode"
ng build bialydunajec-main

echo "==== bialydunajec-frontend-main build!"


echo "==== Building the bialydunajec-frontend-admin application - dev mode"
ng build bialydunajec-admin

echo "==== bialydunajec-frontend-admin build!"
