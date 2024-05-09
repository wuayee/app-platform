/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

export const omml2mathXsl = `<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:m="http://schemas.openxmlformats.org/officeDocument/2006/math"
  xmlns:math="http://www.w3.org/1998/Math/MathML">
  <xsl:output method="html" indent="yes" encoding="UTF-8"/>

    <!-- 模板匹配根节点 -->
    <xsl:template match="/">
      <html>
        <head>
          <style>
            .frac {
              display: inline-block;
              vertical-align: middle;
            }
    
            .frac > span {
              display: block;
              text-align: center;
            }
    
            .frac > span:first-child {
              border-bottom: 1px solid;
            }
            
            .sum {
              display: inline-block;
              vertical-align: middle;
            }
    
            .sum > span {
              display: block;
              text-align: center;
            }
    
            .sum > span:first-child {
              font-size: smaller;
            }
    
            .sum > span:last-child {
              font-size: smaller;
            }
          </style>
        </head>
        <body>
          <xsl:apply-templates/>
        </body>
      </html>
    </xsl:template>
    
  <xsl:template match="/">
    <xsl:apply-templates/>
  </xsl:template>

  <xsl:template match="m:oMath">
    <math>
      <xsl:apply-templates/>
    </math>
  </xsl:template>

<xsl:template match="m:r">
  <mi>
    <xsl:value-of select="m:t"/>
  </mi>
</xsl:template>

<xsl:template match="m:nary">
  <xsl:variable name="chrValue" select="m:naryPr/m:chr/@m:val"/>
  <xsl:variable name="hasSub" select="count(m:sub) > 0"/>
  <xsl:variable name="hasSup" select="count(m:sup) > 0"/>
  <xsl:choose>
    <xsl:when test="($chrValue and contains('&#x2211;&#x220F;&#x2210;&#x22C3;&#x22C2;&#x2A00;&#x2A01;&#x2A04;&#x2A06;&#x2A02;&#x2192;&#x222B;', $chrValue)) and ($hasSub or $hasSup) and not(m:integralPr/m:chr)">
      <munderover>
        <mo>
          <xsl:value-of select="$chrValue"/>
        </mo>
        <xsl:apply-templates select="m:sub"/>
        <xsl:apply-templates select="m:sup"/>
      </munderover>
      <xsl:apply-templates select="m:e"/>
    </xsl:when>

    <xsl:otherwise>
      <mrow>
        <mo>
          <xsl:value-of select="$chrValue"/>
        </mo>
        <msub>
          <xsl:apply-templates select="m:sub"/>
        </msub>
        <msup>
          <xsl:apply-templates select="m:sup"/>
        </msup>
        <xsl:apply-templates select="m:e"/>
      </mrow>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!-- 处理开根号 -->
 <!-- convert m:rad to mrow -->
    <xsl:template match="m:rad">
    <mroot>
      <mrow>
        <xsl:apply-templates select="m:e"/>
      </mrow>
      <mrow>
        <xsl:apply-templates select="m:deg"/>
      </mrow>
    </mroot>
  </xsl:template>
  
  <!-- 极限符号及其下标 -->
<xsl:template match="m:fName[m:limLow]">
<munderover>
    <mo><xsl:value-of select="m:limLow/m:e/m:r/m:t"/></mo>
    <xsl:apply-templates select="m:limLow/m:lim"/>
</munderover>

</xsl:template>

<xsl:template match="m:lim">
    <msub>
      <mi></mi>
      <mi><xsl:value-of select="m:r[1]/m:t"/><xsl:value-of select="m:r[2]/m:t"/><xsl:value-of select="m:r[3]/m:t"/></mi>
    </msub>
</xsl:template>

<!-- 这是一个由 x ⊕ y 组成的数学式子，上面有一条水平长线表示其平均值。其中 ⊕ 表示异或运算符 -->
<xsl:template match="m:bar">
    <mfrac linethickness="1">
          <mrow></mrow>
          <xsl:apply-templates select="m:e" />
    </mfrac>
  </xsl:template>
  
  <!--界定符 {} [] () -->
 <xsl:template match="m:d">
    <mrow>
      <xsl:choose>
        <xsl:when test="m:dPr/m:begChr/@m:val != ''">
          <mo><xsl:value-of select="m:dPr/m:begChr/@m:val"/></mo>
        </xsl:when>
        <xsl:otherwise>
          <mo>(</mo>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="m:dPr/m:begChr/@m:val = '{' and m:e/m:eqArr/m:e">
          <mtable>
            <xsl:apply-templates select="m:e/m:eqArr/m:e"/>
          </mtable>
        </xsl:when>
        <xsl:otherwise>
            <xsl:apply-templates select="m:e"/>
        </xsl:otherwise>
      </xsl:choose>
      <xsl:choose>
        <xsl:when test="m:dPr/m:endChr/@m:val != '' or m:dPr/m:begChr/@m:val = '{'">
          <mo><xsl:value-of select="m:dPr/m:endChr/@m:val"/></mo>
        </xsl:when>
        <xsl:otherwise>
          <mo>)</mo>
        </xsl:otherwise>
      </xsl:choose>
    </mrow>
  </xsl:template>

  <xsl:template match="m:eqArr/m:e">
    <mtr>
      <mtd>
        <xsl:apply-templates select="m:r"/>
      </mtd>
    </mtr>
  </xsl:template>


  
  <!-- 处理 m:e 标签 -->
  <xsl:template match="m:e">
    <mrow>
      <xsl:apply-templates/>
    </mrow>
  </xsl:template>

  <xsl:template match="m:sSup">
    <msup>
      <xsl:apply-templates select="m:e"/>
      <xsl:apply-templates select="m:sup"/>
    </msup>
  </xsl:template>
  
  <xsl:template match="m:sSub">
      <msub>
        <xsl:apply-templates select="m:e"/>
        <xsl:apply-templates select="m:sub"/>
      </msub>
    </xsl:template>

  <xsl:template match="m:chr">
    <mo>
      <xsl:value-of select="@m:val"/>
    </mo>
  </xsl:template>

  <xsl:template match="m:t">
    <mi>
      <xsl:value-of select="."/>
    </mi>
  </xsl:template>

  <xsl:template match="m:sup">
    <msup>
      <xsl:apply-templates select="m:r"/>
      <xsl:apply-templates select="m:sSup"/>
    </msup>
  </xsl:template>

  <xsl:template match="m:sub">
    <msub>
      <xsl:apply-templates select="m:r"/>
      <xsl:apply-templates select="m:sSub"/>
    </msub>
  </xsl:template>

  <xsl:template match="m:f">
    <mfrac>
      <xsl:apply-templates select="m:num"/>
      <xsl:apply-templates select="m:den"/>
    </mfrac>
  </xsl:template>

  <xsl:template match="m:num">
    <mrow>
      <xsl:apply-templates/>
    </mrow>
  </xsl:template>

  <xsl:template match="m:den">
    <mrow>
      <xsl:apply-templates/>
    </mrow>
  </xsl:template>
  
  <!-- 处理分数 -->
<xsl:template match="math:mfrac">
  <span class="frac">
    <span>
      <xsl:apply-templates select="math:numerator"/>
    </span>
    <span>
      <xsl:apply-templates select="math:denominator"/>
    </span>
  </span>
</xsl:template>
<!-- 处理百分比 -->
<xsl:template match="math:mrow[math:mo='%']">
  <span>
    <xsl:apply-templates select="*[not(self::math:mo)]"/>
    <span>%</span>
  </span>
</xsl:template>
<!-- 处理无穷大 -->
<xsl:template match="math:mi[.='∞']">
  <span>&infin;</span>
</xsl:template>


  
  <!-- 处理数学符号和文字 -->
<xsl:template match="math:mi | math:mn | math:mo | math:mtext">
  <span>
    <xsl:value-of select="."/>
  </span>
</xsl:template>

<!-- 处理积分符号 -->
<xsl:template match="math:munderover[math:mo='∫']">
<span>
<span>∫</span>
<sub>
<xsl:apply-templates select="[2]"/>
</sub>
<sup>
<xsl:apply-templates select="[3]"/>
</sup>
</span>
</xsl:template>

<!-- 处理希腊字母 -->
<xsl:template match="math:mi[starts-with(., '&#x') and string-length(.) = 7]">
<span>
<xsl:value-of select="substring(., 4, 3)" disable-output-escaping="yes"/>
</span>
</xsl:template>

<!-- 处理省略号 -->
<xsl:template match="math:mo[.='...']">
<span>…</span>
</xsl:template>

<!-- 处理矩阵 -->
<xsl:template match="math:mtable">

  <table>
    <xsl:apply-templates/>
  </table>
</xsl:template>
<xsl:template match="math:mtr">

  <tr>
    <xsl:apply-templates/>
  </tr>
</xsl:template>
<xsl:template match="math:mtd">

  <td>
    <xsl:apply-templates/>
  </td>
</xsl:template>

  <xsl:template match="none">
    <mprescripts/>
  </xsl:template>
  
</xsl:stylesheet>
`;

