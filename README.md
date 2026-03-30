tttt# Rosetta #
Rosetta is a TSRGv2 mapping generator and updater. Basically a SRG version of [Stitch](https://github.com/FabricMC/stitch).

## Required items
Before using this program, you should know that Rosetta commands involve one or more the following required items:

1. **Config JSON**: JSON file containing specifications for how Rosetta reads and writes data. See the src/test/resources folder for examples.
2. **JAR**: Minecraft JAR file, in its base format as downloaded from Mojang's (or OmniArchive's) servers.
3. **Inheritance JSON**: JSON file that lists inheritance statistics for the JAR. Generated via [NeoForged's InstallerTools](https://github.com/neoforged/InstallerTools).
4. **Match file**: A Match file contains a textual representation of differences between JAR versions. It is generated via FabricMC's [Matcher](https://github.com/FabricMC/Matcher).

## Config parameters
The config JSON file contains 

## Generator
### Usage
`--generate --config <config-file> --jar <jar-file> --inheritance <inheritance-json> --tsrg <tsrg-file> --ids <ids-csv>`

### Key
- `<config-file>`: The config JSON file