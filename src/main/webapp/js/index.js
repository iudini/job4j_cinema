$(document).ready(
    function () {
        $.ajax({
            type: 'GET',
            url: 'http://localhost:8080/cinema/index.do',
            dataType: 'json'
        }).done(function (data) {
            let id = 0;
            for (let i = 1; i < 7; i++) {
                $('#tbody').append(`<tr id="row${i}"><th>${i}</th>`);
                for (let j = 1; j < 7; j++) {
                    let place = `${i}-${j}`;
                    $(`#row${i}`).append(`<td>
                                <input type="radio" value="${place}" id="place-${place}">Ряд ${i} Место ${j}</td>`);
                }
                $('#tbody').append('</tr>');
            }
            setInterval(getOccupiedPlaces, 10000);
            getOccupiedPlaces();
        }).fail(function (err) {
            alert(err);
        });
    }
)

function getOccupiedPlaces() {
    $.ajax({
        type: 'GET',
        url: 'index.do',
        dataType: 'json'
    }).done(function (data) {
        for (let i = 0; i < data.length; i++) {
            let place = $(`#place-${data[i].row}-${data[i].cell}`);
            place.attr('disabled', 'disabled');
            place.parent().attr('occupied','true')
        }
    })
}

function take_place() {
    let result = $('input:radio:checked');
    if (result.length !== 0) {
        let arr = [];
        for (let i = 0; i < result.length; i++) {
            arr.push(result[i].value);
        }
        let sessionId = '1';
        localStorage.setItem("array", arr.toString());
        localStorage.setItem("sessionId", sessionId);
        window.location.href = 'payment.do';
    } else {
        alert("Choose place, please");
        return false;
    }
}
