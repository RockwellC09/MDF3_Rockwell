jQuery(function($){
        JSInterface.getData();
});

function setData(str) {
        var values = str.split(',');
        $('#first_info').text(values[0]);
        $('#last_info').text(values[1]);
        $('#address_info').text(values[2]);
        $('#number_info').text(values[3]);

}