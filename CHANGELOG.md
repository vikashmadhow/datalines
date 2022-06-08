# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.4.0] - 2022-06-08
### Added
- Lines returned by LineReaders changed from List of Objects to Map of Strings
  to Objects, where the key of the map is the location where the value was read.

### Changed
- Version of Apache POI changed to 5.2.2 from 4.1.2.
- Version of jsoup changed to 1.13.1 from 1.14.3.

## [0.3.0] - 2022-05-31
### Added
- `Structure` renamed to `Format`.

### Fixed
- Fixed `IndexOutOfBoundsException` when loading column type for columns in 
  `DelimitedTextLineReader`.

## [0.2.0] - 2022-05-23
### Added
- Ability to guess format from input.
- Input generalised to input streams instead of file only.
- Text data converted to structure types, with fallback to text in case of errors.

## [0.1.1] - 2022-05-20
### Refactored
- `Import` renamed to `Structure`.
- `ImportField` renamed to `Column`.

## [0.1.0] - 2022-05-19
### Added
- Extraction of this library from previous projects and push to GitHub.

