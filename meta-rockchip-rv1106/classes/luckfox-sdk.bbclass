#
# Common Luckfox SDK repository
#

LUCKFOX_SDK_SRCREV ?= "824b817f889c2cbff1d48fcdb18ab494a68f69d1"

SRCREV = "${LUCKFOX_SDK_SRCREV}"

SRC_URI = "git://github.com/LuckfoxTECH/luckfox-pico.git;protocol=https;branch=main;destsuffix=git"

S = "${WORKDIR}/git"