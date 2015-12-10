<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:template match="/">
  <html>
  <body>
    <h2>Context Analysis</h2>
    <table border="1">
		<tr bgcolor="#9acd32">
			<th>Context Name</th>
			<th>Context Data</th>
			<th>Context Decision</th>
		</tr>

    <xsl:for-each select="contextSetType/Context">
		<xsl:variable name="varxs" select="@name"/>
		<xsl:for-each select="ContextDefinition/DefinitionContent">
			<xsl:variable name="varys" select="SenseValue"/>
			<tr>
				<td><xsl:value-of select="$varxs"/></td>
				<td><xsl:value-of select="$varys"/></td>
				<xsl:choose>
					<xsl:when test="not(number($varys))">
						<xsl:choose>
							<xsl:when test="$varys!='Okay'">
								<td>Failed</td>
							</xsl:when>
							<xsl:otherwise>
								<td>Passed</td>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="$varxs='availableWorkers'">
								<xsl:choose>
									<xsl:when test="number($varys) &lt;= 10">
										<td>Low</td>
									</xsl:when>
									<xsl:otherwise>
										<td>High</td>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="number($varys) &gt;= 1000">
										<td>High</td>
									</xsl:when>
									<xsl:otherwise>
										<td>Low</td>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>        
					</xsl:otherwise>
				</xsl:choose>
			</tr>
		</xsl:for-each>
    </xsl:for-each>
    </table>
</body>
</html>
</xsl:template>
</xsl:stylesheet>

