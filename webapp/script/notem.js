/**
 * Add a table sorter to the specified table.
 *
 * @param tableId Identifies the table to be sorted
 * @param defaultSort specifies the default sort column and order
 * @param options additional table options
 */
function setupSimpleTableSort( tableId, defaultSort, options )
{
    // merge caller's options into defaults
    var opts = $.extend( {
        widgets: ['zebra', 'cookie'],
        widgetCookie: {
	        sortList: [ defaultSort ]
	    }
    }, options || {} );

    var selector = '#' + tableId;

    $(document).ready( function() {
        $( selector ).tablesorter( opts );
        $( selector ).addClass("sortedTable");
    });

    if (opts.sortEndFunction != null ) {
        $jq( selector ).bind( "sortEnd", opts.sortEndFunction );
    }
}
