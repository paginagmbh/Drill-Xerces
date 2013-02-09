<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xs="http://www.w3.org/2001/XMLSchema"
	exclude-result-prefixes="xs"
	version="2.0">
	
	
	<!--
		Stylesheets mixes text-nodes up
		
		@author  Marko Hedler
		@date	 2013-02-09
	-->
	
	<xsl:output method="xml"/>
	
	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*"/>
		</xsl:copy>
	</xsl:template>
	
	<!-- only for text nodes on children of the root element -->
	<xsl:template match="/*/*//text()" priority="10">
		
		<xsl:variable name="text" select="for $ch in string-to-codepoints(.) return codepoints-to-string($ch)"/>
		<xsl:variable name="textmod" select="for $i in (1 to count($text)) return if($i mod 2) then $text[$i +1] else  $text[$i -1]"/>
		
		<xsl:value-of select="string-join($textmod,'')"/>
		
	</xsl:template>	
	
</xsl:stylesheet>