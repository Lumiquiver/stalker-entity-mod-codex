# Nightfall Stalkers

A Fabric horror mod for Minecraft 1.20.1. Stalkers emerge only in darkness, watch from a distance, and force unwary players into a fractured dream dimension.

## Features

- **Darkness hunters:** natural spawning is restricted to very low light; stalkers avoid bright areas.
- **Unsettling pursuit:** the entity stares at nearby players, blinks between positions, and becomes aggressive after being observed.
- **Dream trap:** a player caught in darkness is sent to `nightfall:fractured_dream` and receives an in-world warning.
- **Distorted world:** the dream uses familiar Overworld terrain under a permanent midnight sky and reduced ambient light.

## Development

Use JDK 17, then run `gradle runClient` for a development client or `gradle build` to build the JAR. The mod requires Fabric Loader and Fabric API.
