<!DOCTYPE html>
<html lang="pt-br">

<head>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.5.1.min.js"></script>

    <!-- DevExtreme theme -->
    <link rel="stylesheet" href="https://cdn3.devexpress.com/jslib/22.2.3/css/dx.light.css">

    <!-- DevExtreme library -->
    <script type="text/javascript" src="https://cdn3.devexpress.com/jslib/22.2.3/js/dx.all.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/devexpress-diagram/2.1.65/dx-diagram.min.js"></script>

    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>

<body class="dx-viewport">
    <style>
        html {
            background-image: url("../../../../../background0.png");
            transition: background-image 0.5s ease-in-out;
            background-repeat: no-repeat;
            background-size: cover;
            /* Resize the background image to cover the entire container */
            background-size: 100% 2600%;

        }

        span.label {
            color: aliceblue;
        }

        #form {
            margin-top: 25px;
        }

        #select-company-text {
            display: none;
        }

        .options {
            padding: 20px;
            position: absolute;
            bottom: 0;
            right: 0;
            width: 260px;
            top: 0;
            background-color: rgba(191, 191, 191, 0.15);
        }

        .caption {
            font-size: 18px;
            font-weight: 500;
        }

        .option {
            margin-top: 10px;
        }

        .qtdpila{
            border-radius: 5px;
            padding: 10px;
            background-color: black;
            color: green;
        }
        .loader {
  border: 2px solid #f3f3f3; /* Light grey */
  border-top: 2px solid black; /* Blue */
  border-radius: 50%;
  width: 7px;
  height: 7px;
  animation: spin 2s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
    </style>
    <audio id="audio" style="display: none;">
        <source src="../../../../../audioback.mp3" type="audio/mp3">
        Your browser does not support the audio element.
    </audio>
    <div class="login">
        <div id="user"></div>
        <div id="pass"></div>
    </div>
    <div class="popup"></div>
    <div class="demo-container">
        <div id="form-demo">
            <div class="widget-container">
                <div id="select-company-text">Select company:</div>
                <div id="select-company"></div>
                <div id="form"></div>
            </div>
            <div class="options">
                <div class="caption"><span class="label">Options</span></div>
                <div class="option">
                    <span class="label">Iniciar/pausar mineração:</span>
                    <div id="minerar-pila"></div>
                </div>
                <div class="option">
                    <div id="minerar-bloco"></div>
                </div>
                <div class="option">
                    <span class="label">Validar dos cumpadres:</span>
                    <div id="validar-pila"></div>
                </div>
                <div class="option">
                    <div id="validar-bloco"></div>
                </div>
                <div class="option">
                    <span class="label">Transferir:</span>
                    <div id="usuario-transferir"></div>
                </div>
                <div class="option">
                    <div id="valor-transferir"></div>
                </div>
                <div class="option">
                    <div id="carteira" style="display:none">
                    </div>
                    <div id="att-carteira">
                    </div>
                </div>
                <div class="option">
                    <div class="qtdpila">
                        Você possui
                        <div class="num loader"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <script>
        // You can create the Form widget using the following code.
        // Read more at https://js.devexpress.com/Documentation/Guide/Widgets/Common/Advanced/3rd-Party_Frameworks_Integration_API/#Create_and_Configure_a_Widget.
        $(() => {
            initLogin();
        });

        function initLogin() {
            var user = { Usuario: '', Senha: '' }
            const form = $('.login').dxForm({
                labelMode: 'floating',
                formData: user,
                items: [{
                    dataField: 'Usuario',
                }, {
                    dataField: 'Senha',
                    editorOptions: {
                        mode: 'password',
                    }
                },
                {
                    itemType: 'button',
                    horizontalAlignment: 'left',
                    buttonOptions: {
                        text: 'Logar',
                        useSubmitBehavior: true,
                        onClick: () => {
                            initMinerador(0, user, popup);
                            //  popup.hide();
                        }
                    },
                }],
                readOnly: false,
                showColonAfterLabel: true,
                labelLocation: 'left',
                minColWidth: 300,
                colCount: 2,
                position: {
                    at: 'center',
                    my: 'center',
                    collision: 'fit',
                }
            });

            const popup = $('.popup').dxPopup({
                contentTemplate: () => form,
                width: 500,
                height: 280,
                showTitle: true,
                title: 'Login',
                visible: false,
                dragEnabled: false,
                hideOnOutsideClick: false,
                showCloseButton: false,
                position: {
                    at: 'center',
                    my: 'center',
                    collision: 'fit',
                }
            }).dxPopup('instance');

            popup.show();
        }


        function initMinerador(data, user, popup) {
            var users;
            fetch('http://srv-ceesp.proj.ufsm.br:8097/usuario/all').then((response) => response.json()).then((data) => users = data);
            fetch('http://localhost:8080/authenticate', {
                method: "POST",
                body: JSON.stringify({ username: user.Usuario, password: user.Senha }),
                headers: {
                    "Content-type": "application/json; charset=UTF-8"
                }
            })
                .then((response) => response.body)
                .then((rb) => {
                    const reader = rb.getReader();

                    return new ReadableStream({
                        start(controller) {
                            // The following function handles each data chunk
                            function push() {
                                // "done" is a Boolean and value a "Uint8Array"
                                reader.read().then(({ done, value }) => {
                                    // If there is no more data to read
                                    if (done) {
                                        //  console.log('done', done);
                                        controller.close();
                                        return;
                                    }
                                    // Get the data and send it to the browser via the controller
                                    controller.enqueue(value);


                                    // Check chunks by logging to the console
                                    //  console.log(done, value);
                                    push();
                                });
                            }

                            push();
                        },
                    });
                })
                .then((stream) =>
                    // Respond with our stream
                    new Response(stream, { headers: { 'Content-Type': 'text/html' } }).text()
                )
                .then((result) => {
                    if (JSON.parse(result).jwttoken) {
                        initInterface(JSON.parse(result).jwttoken)
                        popup.hide()
                    } else {
                        alert('usuario ou senha inválido')
                    }
                    // Do things with result 
                });

            function initInterface(token) {
                // $(document).click(function () {
                //     $('#audio')[0].play();
                // });

                var count = 0
                setInterval(() => {
                    if (count == 2) {
                        count = 0
                    } else {
                        count++;
                    }
                    $('html').css('background-image', 'url("background' + count + '.png")')

                }, 5000);

                headers = {
                    "Content-type": "application/json; charset=UTF-8"
                    , "Authorization": "Bearer " + token
                }

                var bools = [false, false, false, false]
                var pilasTotais = 0;
                $('#minerar-pila').dxButton({
                    stylingMode: 'contained',
                    text: 'Minerar Pilas',
                    type: 'normal',
                    width: 140,
                    onClick(elem) {
                        bools[0] = bools[0] ? false : true;
                        fetch('http://localhost:8080/pilaBloco/pila', {
                            method: "POST",
                            body: JSON.stringify(bools[0]),
                            headers: headers
                        }).then(e => {
                            if (e.status == 200) {
                                elem.component.option('text', bools[0] ? 'Minerando Pilas' : 'Minerar Pila')
                            }
                        })
                    },
                });

                $('#minerar-bloco').dxButton({
                    stylingMode: 'contained',
                    text: 'Minerar Blocos',
                    type: 'normal',
                    width: 140,
                    onClick(elem) {
                        bools[1] = bools[1] ? false : true;
                        fetch('http://localhost:8080/pilaBloco/bloco', {
                            method: "POST",
                            body: JSON.stringify(bools[1]),
                            headers: headers
                        }).then(e => {
                            if (e.status == 200) {
                                elem.component.option('text', bools[1] ? 'Minerando Blocos' : 'Minerar Blocos')
                            }
                        })
                    },
                });

                $('#validar-pila').dxButton({
                    stylingMode: 'contained',
                    text: 'Validar Pilas',
                    type: 'normal',
                    width: 140,
                    onClick(elem) {
                        bools[2] = bools[2] ? false : true;
                        fetch('http://localhost:8080/pilaBloco/validapila', {
                            method: "POST",
                            body: JSON.stringify(bools[2]),
                            headers: headers
                        }).then(e => {
                            if (e.status == 200) {
                                elem.component.option('text', bools[2] ? 'Validando Pilas' : 'Validar Pilas')
                            }
                        })
                    },
                });

                $('#validar-bloco').dxButton({
                    stylingMode: 'contained',
                    text: 'Validar Blocos',
                    type: 'normal',
                    width: 140,
                    onClick(elem) {
                        bools[3] = bools[3] ? false : true;
                        fetch('http://localhost:8080/pilaBloco/validabloco', {
                            method: "POST",
                            body: JSON.stringify(bools[3]),
                            headers: headers
                        }).then(e => {
                            if (e.status == 200) {
                                elem.component.option('text', bools[3] ? 'Validando Blocos' : 'Validar Blocos')
                            }
                        })
                    },
                });

                $('#usuario-transferir').dxSelectBox({
                    dataSource: users,
                    valueExpr: "id",
                    displayExpr: "nome",
                    onValueChanged(data) {
                    },
                });

                $('#att-carteira').dxButton({
                    stylingMode: 'contained',
                    text: 'Atualizar carteira',
                    type: 'normal',
                    width: 160,
                    onClick(elem) {
                        elem.component.option('text','aguarde...')
                        fetch('http://localhost:8080/pilaBloco/carteira', {
                            method: "GET",
                            headers: headers
                        }).then((response) => response.body)
                            .then((rb) => {
                                const reader = rb.getReader();

                                return new ReadableStream({
                                    start(controller) {
                                        // The following function handles each data chunk
                                        function push() {
                                            // "done" is a Boolean and value a "Uint8Array"
                                            reader.read().then(({ done, value }) => {
                                                // If there is no more data to read
                                                if (done) {
                                                    //  console.log('done', done);
                                                    controller.close();
                                                    return;
                                                }
                                                // Get the data and send it to the browser via the controller
                                                controller.enqueue(value);


                                                // Check chunks by logging to the console
                                                //  console.log(done, value);
                                                push();
                                            });
                                        }

                                        push();
                                    },
                                });
                            })
                            .then((stream) =>
                                // Respond with our stream
                                new Response(stream, { headers: { 'Content-Type': 'text/html' } }).text()
                            )
                            .then((result) => {
                                pilasTotais = JSON.parse(result)
                                $("#carteira").dxDataGrid("instance").option('dataSource',pilasTotais)
                                $('.num').html(' ' + pilasTotais.length + ' pilas')
                                $('.num').removeClass('loader')
                                elem.component.option('text','Atualizar carteira')
                                popup2.show();
                                // Do things with result 
                            });
                    },
                });


                carteira = $('#carteira').dxDataGrid({
                  columns:['assinaturaMaster','chaveCriador','dataCriacao','id','nonce'],
                  width: 700,
                height: 480,
                })
    
                const popup2 = $('.popup').dxPopup({
                contentTemplate: () => {
                    return carteira.show()
                },
                width: 800,
                height: 580,
                showTitle: true,
                title: 'Meus pilas',
                visible: false,
                dragEnabled: false,
                showCloseButton: true,
                position: {
                    at: 'center',
                    my: 'center',
                    collision: 'fit',
                }
            }).dxPopup('instance');

          


            }
        }


    </script>
</body>

</html>