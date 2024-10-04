# bootcamp-java-24-july

<h1 align="center"> Moonlight Hotel</h1> <br>
<p align="center">
    Back end for a Moonlight Hotel System.
</p>

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Testing](#testing)
- [API](#API)


## Introduction
Welcome to Moonlight Hotel. Application in current development.

## Features

### MySQL Docker
Steps to set up:
1. Download and install [Docker Desktop](https://www.docker.com/products/docker-desktop/)
- Ensure the version you install is made for your OS
- Ensure Virtualization is Enabled in the BIOS(Most commonly under CPU/Advanced)
2. Once the Docker Desktop is up and running:
- Open your Terminal and locate projectname\src\main\resources
- Run the command "docker-compose up -d"
- (To turn it off, run the command "docker-compose down")
3. To use the docker, before running the app change the default profile from 'dev' to 'docker'
4. The docker is set to not interfere with a running MySQLService, so it runs on port 3307,
if that is not a free port, make sure you select a free available one in docker-compose.yml
5. You can test using the same endpoints as you do normally.

## Testing
TBD

## API
TBD