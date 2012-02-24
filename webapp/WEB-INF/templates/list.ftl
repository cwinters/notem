<html>
<head>
<title>Notes</title>
<script type="text/javascript">
$(document).ready( function() {
    setupSimpleTableSort( 'notes', [0,0] );
});
</script>
</head>
<body>

<#if it?size == 0>

<p>No notes.</p>

<#else>

<table id="notes" border="1" cellpadding="2" cellspacing="0">
<thead>
    <tr>
        <th width="20%">Date</th>
        <th width="15%">Poster</th>
        <th width="65%">Note</th>
    </tr>
</thead>
<tbody>
<#list it as note>
    <tr valign="top">
        <td width="20%">${note.time.toString( 'yyyy-MM-dd H:mm')}</td>
        <td width="15%">${note.poster}</td>
        <td width="65%">${note.text}</td>
    </tr>
</#list>
</tbody>
</table>

</#if>

</body>
</html>