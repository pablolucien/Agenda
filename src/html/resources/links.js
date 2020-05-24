function toggle(elementId) {
    var ele = parent.body_frame.document.getElementById(elementId);
    var semaphoreValue = + parent.body_frame.document.form1.semaphore.value;
    if(ele.style.display == "block") {
        ele.style.display = "none";
        semaphoreValue = semaphoreValue - 1;
    }
    else {
        ele.style.display = "block";
        semaphoreValue = semaphoreValue + 1;
        parent.body_frame.document.getElementById(elementId + '_div_header').scrollIntoView();
    }
    parent.body_frame.document.form1.semaphore.value = semaphoreValue;
    if (semaphoreValue == 0) {
        parent.body_frame.document.getElementById('Messages').style.display = "block";
    } else {
        parent.body_frame.document.getElementById('Messages').style.display = "none";
    }
}

function toggle_test(elementId) {
    var ele = document.getElementById(elementId);
    if(ele.style.display == "block" || ele.style.display == "inline") {
        ele.style.display = "none";
    }
    else {
        ele.style.display = "inline";
    }
}

function hide(elementId) {
    var ele = parent.body_frame.document.getElementById(elementId);
    ele.style.display = "none";
    var semaphoreValue = +parent.body_frame.document.form1.semaphore.value;
    semaphoreValue = semaphoreValue - 1;
    parent.body_frame.document.form1.semaphore.value = semaphoreValue;
    if (semaphoreValue == 0) {
        parent.body_frame.document.getElementById('Messages').style.display = "block";
    }
}

/* Opens a list of pages (hopefully in new tabs) and displays the first one. */
function showPages(pagesList) {
	// Hay que hacer esta chapucilla porque a firefox (algunas versiones) le da por abrirlas en otro órden.
    var isIE = /*@cc_on!@*/false || !!document.documentMode;
	var isChrome = !!window.chrome; // && !!window.chrome.webstore;
	//alert('!!window.chrome = ' + !!window.chrome);
	//alert('!!window.chrome.webstore = ' + !!window.chrome.webstore);
    var ii;
    if (isIE || isChrome) {
        for (ii = 0; ii < pagesList.length; ii++) {
            window.open(pagesList[ii], '_blank');
        }
    } else {
        for (ii = pagesList.length - 1; ii >= 0; ii--) {
            window.open(pagesList[ii], '_blank');
        }
    }
}

//  Para determinar qué browser es

// // Opera 8.0+
// var isOpera = (!!window.opr && !!opr.addons) || !!window.opera || navigator.userAgent.indexOf(' OPR/') >= 0;
//
// // Firefox 1.0+
// var isFirefox = typeof InstallTrigger !== 'undefined';
//
// // Safari 3.0+ "[object HTMLElementConstructor]" 
// var isSafari = /constructor/i.test(window.HTMLElement) || (function (p) { return p.toString() === "[object SafariRemoteNotification]"; })(!window['safari'] || (typeof safari !== 'undefined' && safari.pushNotification));
//
// // Internet Explorer 6-11
// var isIE = /*@cc_on!@*/false || !!document.documentMode;
//
// // Edge 20+
// var isEdge = !isIE && !!window.StyleMedia;
//
// // Chrome 1+
// var isChrome = !!window.chrome && !!window.chrome.webstore;
//
// // Blink engine detection
// var isBlink = (isChrome || isOpera) && !!window.CSS;