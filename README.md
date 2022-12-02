# Aplicativo Android - BusMobile

![](https://github.com/thibbatista/BusMobile/blob/main/fotosReadMe/foto1.jpg) ![](https://github.com/thibbatista/BusMobile/blob/main/fotosReadMe/foto2.jpg) ![](https://github.com/thibbatista/BusMobile/blob/main/fotosReadMe/foto3.jpg) ![](https://github.com/thibbatista/BusMobile/blob/main/fotosReadMe/foto4.jpg) ![](https://github.com/thibbatista/BusMobile/blob/main/fotosReadMe/foto5.jpg) 

Aplicativo Android para aplicativo que apresenta informações em tempo real do transporte público da cidade São Paulo


## Funcionalidades

* Busca e localização de linhas
* Busca e localização de paradas
* Localização de veículos
* Previsão de chegada dos veículos
* Atualização em tempo real
* Informações sobre linhas, paradas e veículos

## Primeiros Passos

Siga estas instruções para ter uma cópia do projeto funcionando em seu computador.

> Caso não esteja interessado no desenvolvimento, vá para a página [Releases](https://github.com/thibbatista/BusMobile/releases) e baixe e teste diretamento em seu celular android.

### Pré-requisitos

O que você precisará:

```
IDE Android Studio
Android SDK
JDK
Gradle
```

### Instalando

Siga os passos a seguir para rodar esta aplicação.

Cadastre-se e crie uma chave de acesso da Api do google maps.

https://developers.google.com/maps/documentation/android-sdk?hl=pt-br

Cadastre-se e crie uma chave de acesso da Api da SP Trans

https://www.sptrans.com.br/desenvolvedores/api-do-olho-vivo-guia-de-referencia/documentacao-api/

No arquivo AndroidManifest.xml adicione a chave das Api´s

![]![](https://github.com/thibbatista/BusMobile/blob/main/fotosReadMe/manifest.png)


#### Variáveis de ambiente

Informe ao Android Studio o caminho da sua SDK

```
No Windows, vá em Painel de Controle → Sistema e Segurança → Sistema → Configurações avançadas do sistema → Variáveis de Ambiente → Novo
```

Insira a variável de ambiente abaixo:

```
ANDROID_HOME = <<diretório_do_android_sdk>>
```

> Você pode ignorar esta etapa se preferir criar um arquivo local.properties dentro do diretório do projeto para especificar o valor de 'sdk.dir'

#### Obtendo uma cópia

Faça o download, use uma ferramente Git ou a própria IDE Android Studio para clonar este repositório:

```
Na tela de boas vintas do Android Studio, vá em Check out project from Version Control → Git
Informe a URI e clique em Clone, na pergunta sobre criar um projeto do Android Studio, clique em Yes
Marque Create project from existing sources e clique em Next/Yes até finalizar, mantenha as opções padrão
```

### Executando

Execute o projeto:

```
No Android Studio, clique em Run → Run 'app'
```

> A primeira execução irá demorar, pois a IDE irá montar e instalar o APK no dispositivo.


## Deployment

Distribua este projeto como um arquivo *.apk para instalar em um dispositivo Android ou enviar para a Play Store:

```
No Android Studio, com o botão direito sobre o projeto, vá em Build → Build Bundle(s) / APK(s) → Build APK(s)
```

## Tecnologias utilizadas

* [Kotlin](https://kotlinlang.org/)
* [Android Studio](https://developer.android.com/studio)
* [Gradle](https://gradle.org/)
* [XML](https://fontawesome.com/)

## Ferramentas utilizadas

* RecyclerView
* Retrofit
* ViewModel
* Glide


