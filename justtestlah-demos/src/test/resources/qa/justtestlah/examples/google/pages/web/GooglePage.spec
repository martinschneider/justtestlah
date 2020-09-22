# @objects will be populated at runtime based on GooglePage.yaml
# to disable this feature set galen.autoload.locators=false in justtestlah.properties

= Login =

  SEARCH_FIELD:
    below LOGO
    centered horizontally inside viewport
    visible

  LOGO:
    above SEARCH_FIELD
    centered horizontally inside viewport
    width < 100% of SEARCH_FIELD/width
    visible 
