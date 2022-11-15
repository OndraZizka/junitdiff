function ki( fullTestName ) {
    var parts = fullTestName.split(".");

    // org.jboss.package.ShortName -> ShortName
    var shortName = parts[parts.length-1];

    var windowProps = "menubar=no,resizable=yes,scrollbars=yes,status=yes,modal=yes,alwaysRaised=yes";
    //window.open("https://jira.jboss.org/secure/QuickSearch.jspa?searchString=" + shortName,  "jboss-" + fullTestName, windowProps);
    window.open("https://issues.jboss.org/issues/?jql=text%20~%20%22" + shortName + "%22",  "jboss-" + fullTestName,  windowProps);
    //window.open("http://opensource.atlassian.com/projects/hibernate/secure/QuickSearch.jspa?searchString=" + shortName,  "hbn-" + fullTestName, windowProps);
    window.open("http://www.google.com/search?q=" + shortName,  "google-" + fullTestName, windowProps);
}

function ki2( kiHref ) {
    var eTD = kiHref.parentNode;
    var fullTestName = eTD.children[1].textContent + '.' + eTD.children[2].textContent; // FF only!
    ki( fullTestName );
}

function kiA(){ document.write('<a class="known" href="#" onclick="ki2(event.target)">Known issues</a>'); }

function kb_ki( elmKnownIssuesLink ){
    // td { script, span @class="classname" {"..."}, "test" }, td { a, a @href= | }
    var elmTD = elmResultLink.parentNode.parentNode.children[0];
    var testIdent = getTestIdentFrom1stColTd( elmTD );
    alert('This will show known issues for '+testIdent.class+'.'+testIdent.method+'.');
}


/**
 *  Based on the test run link element, gets the classname and testname,
 *  and calls out(). Purpose: Reduces size of the document.
 *
 *  TODO: Replace sGroup with group index (will significantly reduce output size).
 */
function out2( elmResultLink, sGroup ){
    // td { script, span @class="classname" {"..."}, "test" }, td { a | @href=... {"OK"} , a }
    var elm1stTD = elmResultLink.parentNode.parentNode.children[0];
    var testIdent = getTestIdentFrom1stColTd( elm1stTD );

    var elmThisTD = elmResultLink.parentNode;
    var elmFailureDiv = elmThisTD.getElementsByClassName("failure");
    if( elmFailureDiv.size == 0 )
        elmFailureDiv = null;
    else
        elmFailureDiv = elmFailureDiv[0];

    // TODO: Change group references to indexes everywhere.
    sGroup = document.getElementById("groups").children[sGroup-1].getElementsByClassName("path")[0].textContent;
    sResult = elmResultLink.textContent;

    out( testIdent.class, testIdent.method, sGroup, sResult, elmFailureDiv );
}

/**
 *  Gets the classname and testname from the row's first TD. Reduces size.
 */
function getTestIdentFrom1stColTd( elmTD ){
    var elmClassSpan = elmTD.getElementsByClassName("classname")[0];
    var sClass  = elmClassSpan.textContent;
    var sMethod = elmClassSpan.nextSibling.textContent;
    return { class: sClass, method: sMethod };
}


/**
 *  Displays a popup with details of the test - stdout, stderr, failure message etc.
 *  FIXME:  Failure message not being passed here since oskutka's change (r13950).
 *  @param elmFailureDiv  See the failure-content template. May be null.
 */
function out( testClass, testMethod, group, result, elmFailureDiv ){

    var windowProps = "menubar=no,resizable=yes,scrollbars=yes,status=yes,modal=yes,alwaysRaised=yes";
    var win = window.open("", "failure", windowProps);
    var doc = win.document;

    //                  "org.ClassName.method|group"
    var testRunName   = testClass + '.' + testMethod + '|' + group;

    //                  "org.ClassName|group"
    var testSuiteName = testClass + '|' + group;

    if( this.ePopup ){
        this.ePopup.style.display = null;
    }

    //this.ePopup = document.getElementById( testRunName );
    this.ePopup = document.getElementById("popup-div");

    document.getElementById("popup-result").innerHTML = result;

    /*
    var elmData = jQuery(document.getElementById(name));
    var out = elmData.first(".out").html();
    var err = elmData.first(".err").html();
    alert(out);
    jQuery(this.ePopup).first(".out .text").empty().append(out);
    jQuery(this.ePopup).first(".err .text").empty().append(err);
    */

    // Testsuite data from the bottom of the page.
    var elmTSData = document.getElementById( testSuiteName );

    // div class="out">...
    // div class="err">...
    var ori = elmTSData.getElementsByTagName("div")[0].innerHTML;
    var out = elmTSData.getElementsByTagName("div")[1].innerHTML;
    var err = elmTSData.getElementsByTagName("div")[2].innerHTML;
    //alert(out);
    //alert(this.ePopup.getElementsByTagName("div")[2].className);
    //alert(this.ePopup.getElementsByTagName("div")[2].getElementsByTagName("div").length);
    //alert(this.ePopup.getElementsByTagName("div")[2].getElementsByTagName("div")[0]);
    //this.ePopup.getElementsByTagName("div")[1].getElementsByTagName("div")[0].innerHTML = out;
    //this.ePopup.getElementsByTagName("div")[2].getElementsByTagName("div")[0].innerHTML = err;
    this.ePopup.children[1].children[1].innerHTML = ori;
    this.ePopup.children[this.ePopup.children.length-2].children[1].innerHTML = out; //2
    this.ePopup.children[this.ePopup.children.length-1].children[1].innerHTML = err; //3


    elmLocalFail = this.ePopup.getElementsByClassName("failure")[0];
    if ( elmFailureDiv != null ){
        elmLocalFail.innerHTML = elmFailureDiv.innerHTML;
        elmLocalFail.style.display = "block";
    }else{
        elmLocalFail.innerHTML = "";
        elmLocalFail.style.display = "none";
    }
    doc.write('\n        <style>' + document.getElementById('style').innerHTML + '</style>');
    doc.write('\n        <div class="run popup">' + this.ePopup.innerHTML + '</div>');
    doc.close();

    //this.ePopup.style.top = (window.scrollY + 15) + "px";
    //this.ePopup.style.display = "block";
    //this.ePopup.onclick = function(){ this.style.display = null; }

} // out()

/*window.onscroll = function(){
  if( window.ePopup == null ) return;
  window.ePopup.style.top = (window.scrollY + 15) + "px";
}*/

function toggleShowOnlyNonOkTests( bShowOnlyNonOK ){
    //alert( "className: "+ document.getElementById("results-table").getElementsByTagName("tbody")[0].className );
    //alert( "classname: " + ( bShowOnlyNonOK ? "hideOkTests" : "" ) );
    document.getElementById("cbShowOnlyDiffTests").checked=false;
    document.getElementById("results-table").getElementsByTagName("tbody")[0].className = ( bShowOnlyNonOK ? "hideOkTests" : "" );
}

function toggleShowOnlyDiffTests( bShowOnlyNonOK ){
    document.getElementById("cbShowOnlyNonOkTests").checked=false;
    document.getElementById("results-table").getElementsByTagName("tbody")[0].className = ( bShowOnlyNonOK ? "hideNodiffTests" : "" );
}

// TODO: Not working now... the .or class is not being added yet.
// Anyway, I'd rather do this by hiding specific columns, using .r1 class where 1 is the group index.
function toggleShowOnlyNonOkRuns( bShowOnlyNonOK ){
    document.getElementById("results-table").className = ( bShowOnlyNonOK ? "hideOkRuns results" : "results" );
}

function jira( elmAnchor ){
    alert( elmAnchor );
    window.open( "https://jira.jboss.org/secure/QuickSearch.jspa?searchString=" + elmAnchor.innerHTML, "jiraJBoss", "" );
}