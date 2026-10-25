#!/usr/bin/env bash

if [ "$#" -ne 2 ]; then
    echo "Error: You must provide exactly two arguments."
    echo "Usage: $0 <year_number> <quest_number>"
    exit 1
fi

re='^[0-9]+$'

if ! [[ "$1" =~ $re ]] || ! [[ "$2" =~ $re ]]; then
    echo "Error: Both arguments must be valid integers."
    exit 1
fi

req_dir=$1/$2

if [ -d "$req_dir" ]; then
    echo "Directory already exists."
    exit 1
fi

mkdir -p $req_dir
cp template/* $req_dir

echo "Set up for year $1 -> quest $2!"
