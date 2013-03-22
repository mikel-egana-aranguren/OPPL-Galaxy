#!/bin/sh

# Shell script for installing OPPL-Galaxy

# This script assumes that it is living in galaxy-dist/tools/oppl/
# if that is not the case, simply edit the var $jars bellow

jars="../../tool-data/shared/jars/"

cp oppl_galaxy.jar $jars
cp inference.jar $jars
cp query.jar $jars
cp oppl_query.jar $jars
cp merge.jar $jars

cp -r FaCT++-linux-v1.5.2 $jars
cp -r oppl_galaxy_lib $jars
cp -r inference_lib $jars
cp -r query_lib $jars
cp -r oppl_query_lib $jars
cp -r merge_lib $jars

echo 'Now you should edit /galaxy-dist/tool_conf.xml:'
echo '  <section name="Ontology Pre Processor Language" id="oppl">'
echo '    <tool file="oppl/oppl.xml"/>'
echo '    <tool file="oppl/inference.xml"/>'
echo '    <tool file="oppl/query.xml"/>'
echo '    <tool file="oppl/oppl_query.xml"/>'
echo '    <tool file="oppl/merge.xml"/>'
echo '  </section>'

# Foreseen feature: directly edit /galaxy-dist/tool_conf.xml with sed or something