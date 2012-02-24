<html>
<head>
<title>Note</title>
<script type="text/javascript">
$(document).ready( function() {
   $( '#noteForm' ).submit( function() {
       $.cookieJar( 'notem' ).set( 'poster', $( 'input#notePoster' ).val() );
       return true;
   });
    $( 'textarea#noteText' ).focus();
    var jar = $.cookieJar( 'notem', { debug : true } );
    var poster = jar.get( 'poster' );
    if ( poster != null && poster != '' ) {
        $( '#notePoster' ).val( poster );
    }
});    
</script>
</head>
<body>

<form id="noteForm" name="note" method="POST" action="/r/notes">
<table>
    <tr>
        <td>Who?</td>
        <td><input type="text" id="notePoster" name="poster" size="30" /></td>
    </tr>
    <tr>
        <td>What?</td>
        <td>
            <textarea id="noteText" name="text" cols="30" rows="4"></textarea>
        </td>
    </tr>
    <tr>
        <td colspan="2">
            <input type="submit" value="Add" />
        </td>
    </tr>
</table>
</form>

</body>
</html>