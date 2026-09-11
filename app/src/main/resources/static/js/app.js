 fetch('/hello?name=WebFlux')
            .then(res => res.json())
            .then(data => {
                document.getElementById('greeting').innerText = data.message;
            });
    fetch('actuator/health')
    .then(res => res.json())
    .then(data => {
        document.getElementById('health').innerText = data.status
    });