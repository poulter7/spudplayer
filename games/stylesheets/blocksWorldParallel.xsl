		<style type="text/css" media="all">			#fullBlock			{				width:  46px;				height: 46px;				float:	left;				border: 2px solid #FFC;				background-color: #CCCCCC;			}			#emptyBlock			{				width:  46px;				height: 46px;				float:	left;				border: 2px solid #FFC;				background-color: #FFFFFF;			}			#table			{				width:  146px;				height: 6px;				float:	left;				border: 2px solid #FFC;				background-color: #CCCCCC;								}		</style>		<div id="main1" style="position:relative; width:150px; height:160px">					<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='a'">					<xsl:if test="fact[relation='on1' and argument[1]='b' and argument[2]='a']">						<xsl:if test="fact[relation='on1' and argument[1]='c' and argument[2]='b']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">C</p>						</xsl:if>					</xsl:if>					<xsl:if test="fact[relation='on1' and argument[1]='c' and argument[2]='a']">						<xsl:if test="fact[relation='on1' and argument[1]='b' and argument[2]='c']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">B</p>						</xsl:if>					</xsl:if>				</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='b'">					<xsl:if test="fact[relation='on1' and argument[1]='a' and argument[2]='b']">						<xsl:if test="fact[relation='on1' and argument[1]='c' and argument[2]='a']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">C</p>						</xsl:if>					</xsl:if>					<xsl:if test="fact[relation='on1' and argument[1]='c' and argument[2]='b']">						<xsl:if test="fact[relation='on1' and argument[1]='a' and argument[2]='c']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">A</p>						</xsl:if>					</xsl:if>				</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='c'">					<xsl:if test="fact[relation='on1' and argument[1]='a' and argument[2]='c']">						<xsl:if test="fact[relation='on1' and argument[1]='b' and argument[2]='a']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">B</p>						</xsl:if>					</xsl:if>					<xsl:if test="fact[relation='on1' and argument[1]='b' and argument[2]='c']">						<xsl:if test="fact[relation='on1' and argument[1]='a' and argument[2]='b']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">A</p>						</xsl:if>					</xsl:if>				</xsl:if>			</div>			<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='a'">					<xsl:if test="fact[relation='on1' and argument[1]='b' and argument[2]='a']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">B</p>					</xsl:if>						<xsl:if test="fact[relation='on1' and argument[1]='c' and argument[2]='a']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">C</p>					</xsl:if>					</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='b'">					<xsl:if test="fact[relation='on1' and argument[1]='a' and argument[2]='b']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">A</p>					</xsl:if>						<xsl:if test="fact[relation='on1' and argument[1]='c' and argument[2]='b']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">C</p>					</xsl:if>					</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='c'">					<xsl:if test="fact[relation='on1' and argument[1]='a' and argument[2]='c']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">A</p>					</xsl:if>						<xsl:if test="fact[relation='on1' and argument[1]='b' and argument[2]='c']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">B</p>					</xsl:if>					</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='a'">					<xsl:attribute name="id">fullBlock</xsl:attribute>					<p align="center">A</p>				</xsl:if>			</div>			<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='b'">					<xsl:attribute name="id">fullBlock</xsl:attribute>					<p align="center">B</p>				</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table1']/argument='c'">					<xsl:attribute name="id">fullBlock</xsl:attribute>					<p align="center">C</p>				</xsl:if>			</div>			<div id="table"/>		</div>				<div id="spacer" style="width:150px; height:10px;"/>				<div id="main2" style="position:relative; width:150px; height:160px">					<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='a'">					<xsl:if test="fact[relation='on2' and argument[1]='b' and argument[2]='a']">						<xsl:if test="fact[relation='on2' and argument[1]='c' and argument[2]='b']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">C</p>						</xsl:if>					</xsl:if>					<xsl:if test="fact[relation='on2' and argument[1]='c' and argument[2]='a']">						<xsl:if test="fact[relation='on2' and argument[1]='b' and argument[2]='c']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">B</p>						</xsl:if>					</xsl:if>				</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='b'">					<xsl:if test="fact[relation='on2' and argument[1]='a' and argument[2]='b']">						<xsl:if test="fact[relation='on2' and argument[1]='c' and argument[2]='a']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">C</p>						</xsl:if>					</xsl:if>					<xsl:if test="fact[relation='on2' and argument[1]='c' and argument[2]='b']">						<xsl:if test="fact[relation='on2' and argument[1]='a' and argument[2]='c']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">A</p>						</xsl:if>					</xsl:if>				</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='c'">					<xsl:if test="fact[relation='on2' and argument[1]='a' and argument[2]='c']">						<xsl:if test="fact[relation='on2' and argument[1]='b' and argument[2]='a']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">B</p>						</xsl:if>					</xsl:if>					<xsl:if test="fact[relation='on2' and argument[1]='b' and argument[2]='c']">						<xsl:if test="fact[relation='on2' and argument[1]='a' and argument[2]='b']">							<xsl:attribute name="id">fullBlock</xsl:attribute>							<p align="center">A</p>						</xsl:if>					</xsl:if>				</xsl:if>			</div>			<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='a'">					<xsl:if test="fact[relation='on2' and argument[1]='b' and argument[2]='a']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">B</p>					</xsl:if>						<xsl:if test="fact[relation='on2' and argument[1]='c' and argument[2]='a']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">C</p>					</xsl:if>					</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='b'">					<xsl:if test="fact[relation='on2' and argument[1]='a' and argument[2]='b']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">A</p>					</xsl:if>						<xsl:if test="fact[relation='on2' and argument[1]='c' and argument[2]='b']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">C</p>					</xsl:if>					</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='c'">					<xsl:if test="fact[relation='on2' and argument[1]='a' and argument[2]='c']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">A</p>					</xsl:if>						<xsl:if test="fact[relation='on2' and argument[1]='b' and argument[2]='c']">						<xsl:attribute name="id">fullBlock</xsl:attribute>						<p align="center">B</p>					</xsl:if>					</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='a'">					<xsl:attribute name="id">fullBlock</xsl:attribute>					<p align="center">A</p>				</xsl:if>			</div>			<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='b'">					<xsl:attribute name="id">fullBlock</xsl:attribute>					<p align="center">B</p>				</xsl:if>			</div>						<div id="emptyBlock">				<xsl:if test="fact[relation='table2']/argument='c'">					<xsl:attribute name="id">fullBlock</xsl:attribute>					<p align="center">C</p>				</xsl:if>			</div>			<div id="table"/>		</div>		