jQuery(function($){
        JSInterface.getData();
});

/* set the label values to the stored values */
function setData(str) {
        var values = str.split(',');
        $('#first_info').text(values[0]);
        $('#last_info').text(values[1]);
        $('#address_info').text(values[2]);
        $('#number_info').text(values[3]);

}