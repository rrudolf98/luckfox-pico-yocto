SUMMARY = "Luckfox BSP initialization service"
DESCRIPTION = "Initializes Rockchip RV1106/Luckfox BSP kernel modules and multimedia stack."

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

#
# PACKAGECONFIG controls which BSP components are activated by creating
# symlinks in /etc/luckfox-bsp-init.d.
#
# IMPORTANT:
# Enabling a component here does NOT build or install the corresponding
# kernel module. The required kernel drivers must be enabled separately
# in the Linux kernel configuration (CONFIG_*). If a required module is
# unavailable at runtime, luckfox-bsp-init will skip the component and
# print a warning.
#

SRC_URI = "\
    file://luckfox-bsp-init \
    file://luckfox-bsp-init.service \
    file://luckfox-bsp-init.d/ \
"

inherit systemd

S = "${WORKDIR}"

SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "luckfox-bsp-init.service"
SYSTEMD_AUTO_ENABLE:${PN} = "enable"

PACKAGECONFIG ??= "\
    core \
    camera-pipeline \
"

PACKAGECONFIG[core] = ",,,"
PACKAGECONFIG[camera-pipeline] = ",,,"
PACKAGECONFIG[media] = ",,,"
PACKAGECONFIG[rockit] = ",,,"
PACKAGECONFIG[npu] = ",,,"
PACKAGECONFIG[audio] = ",,,"
PACKAGECONFIG[motor] = ",,,"

PACKAGECONFIG[sensor-imx415] = ",,,"
PACKAGECONFIG[sensor-os04a10] = ",,,"
PACKAGECONFIG[sensor-sc4336] = ",,,"
PACKAGECONFIG[sensor-sc3336] = ",,,"
PACKAGECONFIG[sensor-sc530ai] = ",,,"
PACKAGECONFIG[sensor-gc2053] = ",,,"
PACKAGECONFIG[sensor-sc200ai] = ",,,"
PACKAGECONFIG[sensor-sc401ai] = ",,,"
PACKAGECONFIG[sensor-sc450ai] = ",,,"
PACKAGECONFIG[sensor-techpoint] = ",,,"
PACKAGECONFIG[sensor-mis5001] = ",,,"
PACKAGECONFIG[sensor-mia1321] = ",,,"

ACTIVE_CONFS = "\
    ${@bb.utils.contains('PACKAGECONFIG','core','00-core.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','camera-pipeline','20-camera-pipeline.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','media','30-media.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','rockit','40-rockit.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','npu','50-npu.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','audio','60-audio.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','motor','70-motor.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-imx415','10-sensor-imx415.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-os04a10','10-sensor-os04a10.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-sc4336','10-sensor-sc4336.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-sc3336','10-sensor-sc3336.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-sc530ai','10-sensor-sc530ai.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-gc2053','10-sensor-gc2053.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-sc200ai','10-sensor-sc200ai.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-sc401ai','10-sensor-sc401ai.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-sc450ai','10-sensor-sc450ai.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-techpoint','10-sensor-techpoint.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-mis5001','10-sensor-mis5001.conf','',d)} \
    ${@bb.utils.contains('PACKAGECONFIG','sensor-mia1321','10-sensor-mia1321.conf','',d)} \
"

do_install() {
    install -d ${D}${libexecdir}
    install -m0755 \
        ${WORKDIR}/luckfox-bsp-init \
        ${D}${libexecdir}/luckfox-bsp-init

    install -d ${D}${libexecdir}/luckfox-bsp-init.d
    install -m0644 \
        ${WORKDIR}/luckfox-bsp-init.d/*.conf \
        ${D}${libexecdir}/luckfox-bsp-init.d/

    install -d ${D}${sysconfdir}/luckfox-bsp-init.d

    for conf in ${ACTIVE_CONFS}; do
        ln -sf \
            ${libexecdir}/luckfox-bsp-init.d/${conf} \
            ${D}${sysconfdir}/luckfox-bsp-init.d/${conf}
    done

    install -d ${D}${systemd_system_unitdir}
    install -m0644 ${WORKDIR}/luckfox-bsp-init.service \
        ${D}${systemd_system_unitdir}/

        install -d ${D}${sysconfdir}/modprobe.d

    # blacklist for all rk modules that need to be loaded "manually"
    blacklist=${D}${sysconfdir}/modprobe.d/luckfox-bsp-init.conf
    echo "# Automatically generated - do not edit." > ${blacklist}
    echo "# Generated from luckfox-bsp-init.d/*.conf" >> ${blacklist}
    echo >> ${blacklist}

    for conf in ${WORKDIR}/luckfox-bsp-init.d/*.conf; do
        while read module; do
            case "$module" in
                ""|\#*)
                    continue
                    ;;
            esac

            echo "blacklist ${module}" >> ${blacklist}
        done < "$conf"
    done
}

FILES:${PN} += "\
    ${libexecdir}/luckfox-bsp-init \
    ${libexecdir}/luckfox-bsp-init.d \
    ${sysconfdir}/luckfox-bsp-init.d \
    ${sysconfdir}/modprobe.d/luckfox-bsp-init.conf \
"