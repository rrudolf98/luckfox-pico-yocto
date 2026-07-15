inherit module-base kernel-module-split

DEPENDS += "kmod-native"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

# Relative path (from ${S}) to the prebuilt .ko file.
PREBUILT_MODULE ??= ""
MODULE_INSTALL_DIR ??= "${nonarch_base_libdir}/modules/${KERNEL_VERSION}/extra"

do_configure() {
    MODINFO="${RECIPE_SYSROOT_NATIVE}/sbin/modinfo"
    MODULE="${S}/${PREBUILT_MODULE}"

    [ -n "${PREBUILT_MODULE}" ] || bbfatal "PREBUILT_MODULE is not defined"
    [ -x "${MODINFO}" ] || bbfatal "Unable to locate native modinfo"
    [ -f "${MODULE}" ] ||  bbfatal "Kernel module not found: ${MODULE}"

    vermagic=$(${MODINFO} -F vermagic "${MODULE}")

    [ -n "${vermagic}" ] || bbfatal "Unable to read vermagic from ${MODULE}"

    bbnote "Kernel version : ${KERNEL_VERSION}"
    bbnote "Module vermagic: ${vermagic}"

    case "${vermagic}" in
        ${KERNEL_VERSION}*)
            ;;
        *)
            bbfatal "Module was built for '${vermagic}', expected '${KERNEL_VERSION}'"
            ;;
    esac
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${MODULE_INSTALL_DIR}
    install -m 0644 ${S}/${PREBUILT_MODULE} \
        ${D}${MODULE_INSTALL_DIR}/
}