<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
                xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"
                xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006"
                xmlns:math="http://www.w3.org/1998/Math/MathML"
                xmlns:exsl="http://exslt.org/common"
                exclude-result-prefixes="m w mc exsl"
                version="1.0">

    <xsl:output method="xml" indent="yes" omit-xml-declaration="no"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <xsl:apply-templates select="m:oMath"/>
    </xsl:template>

    <xsl:template match="m:oMath">
        <math xmlns="http://www.w3.org/1998/Math/MathML" display="block">
            <xsl:apply-templates/>
        </math>
    </xsl:template>

    <xsl:template match="m:oMathPara">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:oMathPara/m:oMath">
        <math xmlns="http://www.w3.org/1998/Math/MathML" display="block">
            <xsl:apply-templates/>
        </math>
    </xsl:template>

    <xsl:template match="m:r">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:t">
        <mi>
            <xsl:value-of select="."/>
        </mi>
    </xsl:template>

    <xsl:template match="m:rPr">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:sty">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:sSup">
        <msup>
            <xsl:apply-templates select="m:e"/>
            <xsl:apply-templates select="m:sup"/>
        </msup>
    </xsl:template>

    <xsl:template match="m:sup">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:sSub">
        <msub>
            <xsl:apply-templates select="m:e"/>
            <xsl:apply-templates select="m:sub"/>
        </msub>
    </xsl:template>

    <xsl:template match="m:sub">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:sSubSup">
        <msubsup>
            <xsl:apply-templates select="m:e"/>
            <xsl:apply-templates select="m:sub"/>
            <xsl:apply-templates select="m:sup"/>
        </msubsup>
    </xsl:template>

    <xsl:template match="m:sSupPr">
        <mstyle scriptlevel="1">
            <xsl:apply-templates/>
        </mstyle>
    </xsl:template>

    <xsl:template match="m:sSubPr">
        <mstyle scriptlevel="1">
            <xsl:apply-templates/>
        </mstyle>
    </xsl:template>
    <xsl:template match="m:sSup">
        <msup>
            <xsl:apply-templates select="m:e"/>
            <mstyle scriptlevel="1">
                <xsl:choose>
                    <xsl:when test="count(m:sup) = 1">
                        <xsl:apply-templates select="m:sup"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <mrow>
                            <xsl:apply-templates select="m:sup"/>
                        </mrow>
                    </xsl:otherwise>
                </xsl:choose>
            </mstyle>
        </msup>
    </xsl:template>
    <xsl:template match="m:sSub">
        <msub>
            <xsl:apply-templates select="m:e"/>
            <mstyle scriptlevel="1">
                <xsl:choose>
                    <xsl:when test="count(m:sub) = 1">
                        <xsl:apply-templates select="m:sub"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <mrow>
                            <xsl:apply-templates select="m:sub"/>
                        </mrow>
                    </xsl:otherwise>
                </xsl:choose>
            </mstyle>
        </msub>
    </xsl:template>


    <xsl:template match="m:rad">
        <mroot>
            <xsl:apply-templates select="m:deg"/>
            <xsl:apply-templates select="m:e"/>
        </mroot>
    </xsl:template>

    <xsl:template match="m:deg">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:f">
        <mfrac>
            <xsl:apply-templates select="m:num"/>
            <xsl:apply-templates select="m:den"/>
        </mfrac>
    </xsl:template>

    <xsl:template match="m:num">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:den">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:func">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:lim">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:limLow">
        <msub>
            <xsl:apply-templates select="m:lim"/>
            <xsl:apply-templates select="m:low"/>
        </msub>
    </xsl:template>

    <xsl:template match="m:low">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:limUpp">
        <msup>
            <xsl:apply-templates select="m:lim"/>
            <xsl:apply-templates select="m:upp"/>
        </msup>
    </xsl:template>

    <xsl:template match="m:upp">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:box">
        <mrow>
            <xsl:apply-templates/>
        </mrow>
    </xsl:template>

    <xsl:template match="m:bar">
        <mrow>
            <xsl:apply-templates/>
        </mrow>
    </xsl:template>

    <xsl:template match="m:groupChr">
        <mo>
            <xsl:value-of select="@m:chr"/>
        </mo>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:acc">
        <mover>
            <xsl:apply-templates select="m:e"/>
            <mo>
                <xsl:value-of select="@m:chr"/>
            </mo>
        </mover>
    </xsl:template>

    <xsl:template match="m:chr">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="m:borderBox">
        <menclose notation="box">
            <xsl:apply-templates/>
        </menclose>
    </xsl:template>

    <xsl:template match="m:strikeH">
        <menclose notation="horizontalstrike">
            <xsl:apply-templates/>
        </menclose>
    </xsl:template>

    <xsl:template match="m:strikeV">
        <menclose notation="verticalstrike">
            <xsl:apply-templates/>
        </menclose>
    </xsl:template>

    <xsl:template match="m:strikeD">
        <menclose notation="updiagonalstrike">
            <xsl:apply-templates/>
        </menclose>
    </xsl:template>

    <xsl:template match="m:strikeD">
        <menclose notation="downdiagonalstrike">
            <xsl:apply-templates/>
        </menclose>
    </xsl:template>

    <xsl:template match="m:nary">
        <munderover>
            <mo>
                <xsl:value-of select="@m:chr"/>
            </mo>
            <xsl:apply-templates select="m:sub"/>
            <xsl:apply-templates select="m:sup"/>
        </munderover>
        <xsl:apply-templates select="m:e"/>
    </xsl:template>

    <xsl:template match="m:phantom">
        <mphantom>
            <xsl:apply-templates/>
        </mphantom>
    </xsl:template>

    <xsl:template match="m:spc">
        <mspace width="{@m:space}em"/>
    </xsl:template>

    <xsl:template match="m:row">
        <mrow>
            <xsl:apply-templates/>
        </mrow>
    </xsl:template>

    <xsl:template match="m:delim">
        <mo>
            <xsl:value-of select="@m:begChr"/>
        </mo>
        <xsl:apply-templates/>
        <mo>
            <xsl:value-of select="@m:endChr"/>
        </mo>
    </xsl:template>
</xsl:stylesheet>





