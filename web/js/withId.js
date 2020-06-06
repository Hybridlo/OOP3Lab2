function withId() {
    var to = document.getElementById('patientNum').value;
    if (to === 0) {
        return;
    }
    window.location.href += "?id=" + to;
}