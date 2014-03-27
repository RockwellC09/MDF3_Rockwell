jQuery(function($){
   $("#num").mask("(999) 999-9999");
   $("#errors").hide();
});

function populate() {
        
}

function checkData () {
        /* check for black fields */
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
                hasErrors = false;          
        }
        if (lastField.value.trim() == "") {
                lastField.style.borderColor = "red";
                $("#errors").append("<p>Please enter your last name.</p>");
                hasErrors = true;
        } else {
                lastField.style.borderColor = "#C36146";  
                hasErrors = false;
        }
        if (addressField.value.trim() == "") {
                addressField.style.borderColor = "red";
                $("#errors").append("<p>Please enter your address.</p>");
                hasErrors = true;
        } else {
                addressField.style.borderColor = "#C36146"; 
                hasErrors = false;
        }
        if (numField.value.trim() == "" || numField.value.length > 14) {
                numField.style.borderColor = "red";
                $("#errors").append("<p>Please enter a valid phone number.</p>");
                hasErrors = true;
        } else {
                numField.style.borderColor = "#C36146"; 
                hasErrors = false;
        } 
        
        if (!hasErrors) {
                $("#errors").hide();
                JSInterface.saveData(firstField.value, lastField.value, addressField.value, numField.value);
        } else {
                $("#errors").show();
        }
                  
}
