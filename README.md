# Notificação — Android

App nativo simples e offline, Android 8 ou superior. Escolha Cakto, Kiwify, Eduzz ou Hotmart, informe um valor e toque em **Gerar notificação**.

Título: **Venda Aprovada!**
Texto: **Valor: R$ 7,87**
Contexto: **Kiwify** (conforme a seleção).

O Android exibe a origem **Notificação**. As plataformas usam avatares ilustrativos com iniciais e cores, sem logotipos oficiais ou afiliação. Não é comprovante de pagamento nem integração com as plataformas.

## Obter o APK pelo GitHub, inclusive no celular

1. Crie um repositório e envie o conteúdo desta pasta, incluindo `.github/workflows/android.yml`, na raiz.
2. Abra a aba **Actions → Gerar APK → Run workflow** (ou aguarde o build do push na branch main/master).
3. Depois que terminar com sucesso, abra a execução e baixe **VendaDemo-APK** em Artifacts. Extraia o ZIP para encontrar `app-debug.apk`.
4. Transfira/abra o APK no Android e permita instalação dessa origem, se solicitado.
5. Abra o app e permita notificações ao tocar em Gerar.

O APK é de desenvolvimento, assinado com chave debug. Builds de execuções diferentes podem exigir desinstalar a versão anterior, perdendo as preferências locais. Para distribuição estável, configure uma chave de release própria.

## Android Studio

Abra esta pasta, use JDK 17, SDK 35 e Gradle 8.9 na configuração local do Gradle. Não há Gradle Wrapper incluído; use uma instalação local do Gradle 8.9 ou gere o wrapper com `gradle wrapper --gradle-version 8.9`. Execute `gradle assembleDebug lintDebug`. APK: `app/build/outputs/apk/debug/app-debug.apk`.

## Comportamento

- Aceita `7,87`, `7.87` ou `10`; máximo 7 dígitos inteiros e 2 casas decimais; rejeita zero, negativos e separadores de milhares.
- Lembra a última plataforma e o valor.
- Gera notificações distintas e permite abrir o app ao tocar nelas.
- Android 13+ pede permissão; bloqueios do app/canal têm caminho para configurações.
- Exibição de banner e som depende das configurações do sistema, canal e Não Perturbe.
- Não usa internet, conta, servidor ou acesso a outros aplicativos.

## Verificação desta entrega

XML analisado e estrutura do ZIP verificada. A validação de valores foi revisada no código, mas o teste Java não foi executado porque este ambiente não possui javac. Não foi possível compilar o APK ou testar em dispositivo: o ambiente de criação não possui SDK Android nem Gradle. O workflow de compilação está incluído, mas não foi executado nesta entrega.

A notificação usa o nome do app e o subtexto da plataforma (por exemplo, Notificação • Kiwify). Separadores e posição variam conforme a versão do Android. O texto não inclui o marcador Simulação.
