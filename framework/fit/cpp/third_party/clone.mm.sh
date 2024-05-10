#!/bin/sh
set -eu

CURRENT_DIR=$(cd "$(dirname "$0")" || exit; pwd)
XMLFILE=third_party.mm.xml

cd "${CURRENT_DIR}"
rm -rf tmp && mkdir -p tmp
cp -a "${XMLFILE}" tmp/
cd tmp/
git init .
git config user.email "tempemail"
git config user.name "tempname"
git add "${XMLFILE}" && git commit -m "add"

cd  "${CURRENT_DIR}"
rm -rf .mm

git mm init -u ${CURRENT_DIR}/tmp -m ${XMLFILE} || (rm -rf ${CURRENT_DIR}/tmp/.git && git mm init -u ${CURRENT_DIR}/tmp -m ${XMLFILE})
git mm sync -d --force-sync