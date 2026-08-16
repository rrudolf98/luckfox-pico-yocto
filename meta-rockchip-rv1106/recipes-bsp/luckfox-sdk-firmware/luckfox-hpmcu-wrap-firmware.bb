SUMMARY = "Luckfox HPMCU firmware"
DESCRIPTION = "HPMCU firmware from the Luckfox SDK"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "(luckfox-pico-ultra-w)"

inherit luckfox-sdk

do_compile[noexec] = "1"
do_configure[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}/firmware

    install -m 0644 \
        ${S}/sysdrv/drv_ko/rockit/release_rockit-ko_rv1106_arm/hpmcu_wrap.bin \
        ${D}${nonarch_base_libdir}/firmware/
}

FILES:${PN} += "${nonarch_base_libdir}/firmware/hpmcu_wrap.bin"
