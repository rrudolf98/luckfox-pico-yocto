SUMMARY = "Luckfox MPP Vcodec kernel module"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "(luckfox-pico-ultra-w)"

inherit luckfox-sdk
inherit luckfox-prebuilt-module

DEPENDS += "linux-rockchip-rv1106"

PREBUILT_MODULE = "sysdrv/drv_ko/kmpp/release_kmpp_rv1106_arm/mpp_vcodec.ko"

PACKAGES:remove = "${PN}-dbg ${PN}-dev"