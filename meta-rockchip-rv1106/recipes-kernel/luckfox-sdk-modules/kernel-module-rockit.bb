SUMMARY = "Luckfox Rockit kernel module"
LICENSE = "CLOSED"

COMPATIBLE_MACHINE = "(luckfox-pico-ultra-w)"

inherit luckfox-sdk
inherit luckfox-prebuilt-module

DEPENDS += "linux-rockchip-rv1106 luckfox-hpmcu-wrap-firmware"

PREBUILT_MODULE = "sysdrv/drv_ko/rockit/release_rockit-ko_rv1106_arm/rockit.ko"

# Remove incorrect dependency on kernel-module-venc-demo.
# The prebuilt Rockit module advertises it via modinfo, but depmod
# does not resolve it and modprobe loads the module without it.
python populate_packages:append() {
    kv = d.getVar("KERNEL_VERSION")

    pkg = "kernel-module-rockit-%s" % kv
    bad = "kernel-module-venc-demo-%s" % kv

    rdeps = (d.getVar("RDEPENDS:" + pkg) or "").split()

    if bad in rdeps:
        rdeps.remove(bad)
        d.setVar("RDEPENDS:" + pkg, " ".join(rdeps))
        bb.note("Removed incorrect dependency '%s'" % bad)
}