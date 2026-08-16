
# Provides support for installing a prebuilt Linux kernel module (.ko)
# (prebuilt in luckfox SDK).
#
# The class verifies that the prebuilt module was built for the same
# kernel version as the kernel currently being built. The module is
# then installed into the kernel's "extra" modules directory.

inherit module-base kernel-module-split

# kmod-native provides modinfo used to validate the prebuilt module.
DEPENDS += "kmod-native"

# The module is already built and must not be modified by BitBake.
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# Relative path to the prebuilt .ko file from ${S}.
PREBUILT_MODULE ??= ""

# Installation directory for the prebuilt kernel module.
MODULE_INSTALL_DIR ??= "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra"

do_configure() {
    MODINFO="${RECIPE_SYSROOT_NATIVE}/sbin/modinfo"
    MODULE="${S}/${PREBUILT_MODULE}"

    [ -n "${PREBUILT_MODULE}" ] || \
        bbfatal "PREBUILT_MODULE is not defined"

    [ -x "${MODINFO}" ] || \
        bbfatal "Unable to locate native modinfo: ${MODINFO}"

    [ -f "${MODULE}" ] || \
        bbfatal "Prebuilt kernel module not found: ${MODULE}"

    # Extract the kernel version and configuration flags embedded
    # in the prebuilt module.
    vermagic="$(${MODINFO} -F vermagic "${MODULE}")"

    [ -n "${vermagic}" ] || \
        bbfatal "Unable to read vermagic from ${MODULE}"

    bbnote "Kernel version : ${KERNEL_VERSION}"
    bbnote "Module vermagic: ${vermagic}"

    # The vermagic starts with the kernel version. Additional flags,
    # such as SMP, preempt and mod_unload, may follow it.
    case "${vermagic}" in
        ${KERNEL_VERSION}*)
            bbnote "Prebuilt kernel module is compatible with ${KERNEL_VERSION}"
            ;;
        *)
            bbfatal "Module was built for '${vermagic}', expected kernel version '${KERNEL_VERSION}'"
            ;;
    esac
}

# The kernel module is prebuilt, so there is nothing to compile.
do_compile[noexec] = "1"

do_install() {
    install -d "${D}${MODULE_INSTALL_DIR}"

    install -m 0644 \
        "${S}/${PREBUILT_MODULE}" \
        "${D}${MODULE_INSTALL_DIR}/"
}