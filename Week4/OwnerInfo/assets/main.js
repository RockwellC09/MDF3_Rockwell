jQuery(function($){
        $("#errors").hide();
        $("#num").mask("(999) 999-9999");
        JSInterface.getDataInput();
});

/* populate input fields with stored data */
function setDataInput(str) {
        var values = str.split(',');
        $('#first').val(values[0]);
        $('#last').val(values[1]);
        $('#adr').val(values[2]);
        $('#num').val(values[3]);
}

function checkData () {
        /* check for blank fields */
        $("#errors").empty();
        var firstField = document.getElementById("first");
        var lastField = document.getElementById("last");
        var addressField = document.getElementById("adr");
        var numField = document.getElementById("num");
        var hasErrors = false;
        if (firstField.value.trim() == "") {
                firstField.style.borderColor = "red";
                $("#errors").append("<p>Please enter your first name.</p>");
                hasErrors = true;
        } else {
                firstField.style.borderColor = "#C36146";          
        }
        if (lastField.value.trim() == "") {
                lastField.style.borderColor = "red";
                $("#errors").append("<p>Please enter your last name.</p>");
                hasErrors = true;
        } else {
                lastField.style.borderColor = "#C36146";  
        }
        if (addressField.value.trim() == "") {
                addressField.style.borderColor = "red";
                $("#errors").append("<p>Please enter your address.</p>");
                hasErrors = true;
        } else {
                addressField.style.borderColor = "#C36146"; 
        }
        if (numField.value.trim() == 0) {
                numField.style.borderColor = "red";
                $("#errors").append("<p>Please enter a valid phone number.</p>");
                console.log(numField.value.length);
                hasErrors = true;
        } else {
                numField.style.borderColor = "#C36146"; 
        } 
        
        if (!hasErrors) {
                $("#errors").hide();
                JSInterface.saveData(firstField.value, lastField.value, addressField.value, numField.value);
        } else {
                $("#errors").show();
                hasErrors = false;
        }
                  
}

/* show or hide the info message */
function showHide() {
        if ($(".bubble").is(":visible") ) {
                $(".bubble").slideUp();
        } else {
                $(".bubble").slideDown()
        }
}
