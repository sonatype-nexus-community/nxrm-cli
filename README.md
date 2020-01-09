# Nxrm Command Line Application
Main purpose of this tool is to ease upload of multiple packages to different NXRM repositories using automatic jobs.

This is just the proof of concept, feel free to write issues/suggestions.

## Supported formats for now:
 - yum
 - apt
 - npm
 - rubygems
 - nuget
 - pypi
 
## Usage
**Use `help` command to show all commands and options of the tool.**

`java -jar ./nxrm-cli-jar-with-dependencies.jar  help`


#### User flow example:
Uploading files from debian folder to local APT hosted repository:

`java -jar ./nxrm-cli-jar-with-dependencies.jar  add --path=/Users/MyUser/Desktop/debian --changelist-name=apt-test --pattern=".*.deb"`

`java -jar ./nxrm-cli-jar-with-dependencies.jar  status`

`java -jar ./nxrm-cli-jar-with-dependencies.jar  config --add-repo=apt-test-hosted --format=apt --nexus-host=http://localhost:1234/ --user=user --password=password`

`java -jar ./nxrm-cli-jar-with-dependencies.jar  push --repo-name=apt-test-hosted --changelist-name=apt-test --tag=testUpload`

## Issues
During PoC implementation some issues were faced:
 - Component API has limited supported repository format types.
 - To upload asset/component for some repositories format-specific data must be calculated on client side. 
 For example, to upload maven2 component, group, name and version must be extracted from package to be provided during 
 upload.

Suggestions to implement on Nexus side:
 - Cover all repository formats in component API.
 - Extract format-specific data utils to be used in CLI application (by creating some API or extracting to 
 another module to be added to CLI as dependency).
 - Implement multiple file upload API.
 
Suggestions to implement on the CLI tool side:
 - Replace CLI code with some library (Apache Commons CLI, for example).
 - Consider improving project architecture.
