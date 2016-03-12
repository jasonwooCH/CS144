
/**
 * Provides suggestions for state names (USA).
 * @class
 * @scope public
 */
 
function StateSuggestions() {

}


/**
 * Request suggestions for the given autosuggest control. 
 * @scope protected
 * @param oAutoSuggestControl The autosuggest control to provide suggestions for.
 */
StateSuggestions.prototype.requestSuggestions = function (oAutoSuggestControl /*:AutoSuggestControl*/,
                                                          bTypeAhead /*:boolean*/) {
    var aSuggestions = [];
    var sTextboxValue = oAutoSuggestControl.textbox.value;

    if (sTextboxValue.length > 0){

        var request = new XMLHttpRequest();
        request.onreadystatechange = function(){
            if(request.readyState == 4){
                if(request.status == 200){
                    var suggestions = request.responseXML.getElementsByTagName("suggestion");
                    for(var i=0; i<suggestions.length; i++){
                        aSuggestions.push(suggestions[i].getAttribute("data"));
                    }      
                    oAutoSuggestControl.autosuggest(aSuggestions, false);
                }

            }
        }
        request.open("GET","suggest?q="+sTextboxValue, true);
        request.send();
    }
};