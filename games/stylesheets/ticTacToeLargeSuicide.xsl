		<style type="text/css" media="all">			#cell			{				width:  46px;				height: 46px;				float:	left;				border: 2px solid #FFC;				background-color: #CCCCCC;			}		</style>		<!-- Draw Board -->		<div id="main" style="position:relative; width:250px; height:250px">			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<!-- Draw Marks -->			<xsl:for-each select="fact[relation='mark']">				<xsl:variable name="x" select="50 * (./argument[2]-1)"/>				<xsl:variable name="y" select="50 * (./argument[1]-1)"/>				<xsl:variable name="mark" select="./argument[3]"/>								<div>					<xsl:attribute name="style">						<xsl:value-of select="concat('position:absolute; left:', $x ,'px; top:', $y ,'px; width:50px; height:50px;')"/>					</xsl:attribute>							<p align="center"><xsl:value-of select="$mark"/></p>				</div>							</xsl:for-each>		</div>		