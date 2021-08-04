$(document).ready(
    function () {
        let arr = localStorage.getItem("array").split(',');
        for (let i = 0; i < arr.length; i++) {
            let row = arr[i].split('-')[0];
            let cell = arr[i].split('-')[1];
            $('#header').append(`Вы выбрали Ряд <span id="row">${row}</span> Место <span id="cell">${cell}</span><br/>`)
        }
        let sum = arr.length * 500;
        $('#header').append(`Сумма <span id="sum">${sum}</span>`);
    }
)

function pay() {
    $.ajax({
        method: 'POST',
        url: `http://localhost:8080/cinema/payment.do`,
        data: {
            arr: localStorage.getItem("array"),
            // row: localStorage.getItem("row"),
            // cell: localStorage.getItem("cell"),
            sessionId: localStorage.getItem("sessionId"),
            username: $('#username').val(),
            phone: $('#phone').val(),
            email: $('#email').val()
        },
        dataType: 'json'
    }).done(function(data) {
        alert(data);
        window.location.href = '/cinema/';
    });
}