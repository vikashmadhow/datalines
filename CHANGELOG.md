# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.6.1]- 2023-11-23
### Added
- `build.grade` updated to be compatible with Gradle version 8.

## [0.6.0] - 2023-06-02
### Added
- Set column to default value when source does not contain a non-empty value and
  there is a default value defined in the format for that column.

## [0.5.6] - 2023-01-31
### Added
- `isText` method added to `Column` returning true if the column is of text type.  

## [0.5.5] - 2022-12-03
### Added
- `label` can be specified using lowercase attribute names (`short_label` and 
  `label` as well as `SHORT_LABEL` or `LABEL` previously). This aligns with how
  these attributes are named elsewhere in ESQL projects.

## [0.5.4] - 2022-10-03
### Fixed
- Resolved several NPE in readers when format provided is null (i.e. when latter
  is to be guessed). 

## [0.5.3] - 2022-09-25
### Fixed
- Fix `FixedLengthTextLineReader` `supports` method which now checks if the 
  location is not null before checking if it starts with `[`; this prevents a 
  NPE when the location of some columns are not specified. 

## [0.5.2] - 2022-09-20
### Added
- Default column separators for delimited text data (e.g. CSV files) changed to
  include tab (\t) in addition to comma (',').

## [0.5.1] - 2022-08-12
### Added
- `attribute` method added to `Column` returning the value of the attribute, if 
  available, or a default value (null if not provided) otherwise.
- `label` method added to `Column` returning A human-readable label for the 
  column, specified as the `SHORT_LABEL` or `LABEL` attribute; if these are not 
  provided, the label is derived by expanding the name of the column.
- Column attribute values are translated to expected type (from string).

## [0.5.0] - 2022-06-16
### Added
- Xls reader produces numeric values conforming to Xlsx reader.
- Xls reader correctly estimates number of lines in sheet.
- Xls reader tests.

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

