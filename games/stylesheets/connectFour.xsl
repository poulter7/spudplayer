		<style type="text/css" media="all">						#cell			{				width:  46px;				height: 46px;				float:	left;				border: 2px solid #FFC;				background-color: #CCCCCC;			}		</style>		<!-- Draw Board -->		<div id="main" style="position:relative; width:400px; height:300px;">			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/><div id="cell"/>			<!-- Draw Marks -->			<xsl:for-each select="fact[relation='cell']">				<xsl:variable name="x" select="50 * (./argument[1]-1)"/>				<xsl:variable name="y" select="50 * (6-./argument[2]) + 4"/>								<div>					<xsl:attribute name="style">						<xsl:value-of select="concat('position:absolute; left:', $x ,'px; top:', $y ,'px; width:50px; height:50px;')"/>					</xsl:attribute>							<xsl:if test="./argument[3]='red'">						<img src="/docserver/gamemaster/images/rc.gif"/>					</xsl:if>					<xsl:if test="./argument[3]='black'">						<img src="/docserver/gamemaster/images/bcc.gif"/>					</xsl:if>										</div>							</xsl:for-each>		</div>		