				<style type="text/css" media="all">					#main					{						position:   relative;						width:      350px;						height:     350px;						padding:    10px;						border:     2px solid #b17735;						background: transparent url(/docserver/gamemaster/images/ccboard.gif) repeat top left; 					}							div.teal					{						width: 50px;						height: 40px;						background: #00ffff;					}					div.red					{						width: 50px;						height: 40px;						background: #ff0000;					}					div.blue					{						width: 50px;						height: 40px;						background: #0000ff;					}					div.green					{						width: 50px;						height: 40px;						background: #00ff00;					}					div.yellow					{						width: 50px;						height: 40px;						background: #ffff00;					}					div.magenta					{						width: 50px;						height: 40px;						background: #ff00ff;					}					div.blank					{						width: 50px;						height: 40px;						background: #cca083;					}					#a1,#i1					{						margin-left: 150px;						clear: left;					}					#b1,#h1					{						margin-left: 125px;						float: left;						clear: left;					}					#c1,#g1					{						margin-left: 0px;						clear: left;						float: left;					}					#d1,#f1					{						margin-left: 25px;						clear: left;						float: left;					}					#e1					{						margin-left: 50px;						clear: left;						float: left;					}									</style>				<div id="main">					<xsl:for-each select="fact[relation='cell']">						<xsl:sort select="argument[1]"/>							<div style="float: left">								<xsl:attribute name="id"><xsl:value-of select="argument[1]"/></xsl:attribute>								<xsl:choose>								    <xsl:when test="argument[2]='yellow'">										<xsl:attribute name="class">yellow</xsl:attribute>										<img src="/docserver/gamemaster/images/boardpit.gif"/>									</xsl:when>								    <xsl:when test="argument[2]='magenta'">										<xsl:attribute name="class">magenta</xsl:attribute>										<img src="/docserver/gamemaster/images/boardpit.gif"/>								    </xsl:when>								    <xsl:when test="argument[2]='teal'">										<xsl:attribute name="class">teal</xsl:attribute>										<img src="/docserver/gamemaster/images/boardpit.gif"/>								    </xsl:when>								    <xsl:when test="argument[2]='green'">										<xsl:attribute name="class">green</xsl:attribute>										<img src="/docserver/gamemaster/images/boardpit.gif"/>								    </xsl:when>								    <xsl:when test="argument[2]='red'">										<xsl:attribute name="class">red</xsl:attribute>										<img src="/docserver/gamemaster/images/boardpit.gif"/>								    </xsl:when>								    <xsl:when test="argument[2]='blue'">										<xsl:attribute name="class">blue</xsl:attribute>										<img src="/docserver/gamemaster/images/boardpit.gif"/>								    </xsl:when>								    <xsl:otherwise>										<xsl:attribute name="class">blank</xsl:attribute>										<img src="/docserver/gamemaster/images/boardpitb.gif"/>								  	</xsl:otherwise>							</xsl:choose>						</div>					</xsl:for-each>				</div>		