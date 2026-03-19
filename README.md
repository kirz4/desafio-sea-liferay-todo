# 📝 Desafio SEA — Todo App com Liferay

Aplicação de gerenciamento de tarefas desenvolvida com **Liferay**, organizada em módulos OSGi e pensada para demonstrar arquitetura, regras de negócio, upload de arquivos, hierarquia de subtarefas e testes automatizados.

Repositório oficial:

```bash
git clone https://github.com/kirz4/desafio-sea-liferay-todo.git
cd desafio-sea-liferay-todo
```

---

## 📌 Visão Geral

O projeto implementa um sistema de **tarefas e subtarefas** com:

- criação, edição e exclusão de tarefas
- marcação de tarefas como concluídas ou pendentes
- criação de subtarefas vinculadas a uma tarefa principal
- upload de imagens para tarefas e subtarefas
- filtros por status
- contagem e exibição correta de subtarefas nas abas
- testes unitários para service e MVCActionCommands principais

Além da entrega funcional, o projeto foi evoluído com foco em:

- **arquitetura modular**
- **testabilidade**
- **tratamento de erros reais**
- **boas práticas para entrevista técnica**

---

## ✨ Funcionalidades

### Tarefas
- Criar tarefa
- Editar tarefa
- Excluir tarefa
- Alternar status entre:
  - pendente
  - concluída

### Subtarefas
- Criar subtarefas ligadas a uma tarefa pai
- Exibição hierárquica na interface
- Edição e exclusão de subtarefas
- Filtros considerando a hierarquia completa

### Imagens
- Upload de imagem em tarefas e subtarefas
- Persistência via **Liferay Document Library**
- Tratamento de nomes duplicados usando UUID para evitar conflito de arquivos

### Filtros
- Todas
- Pendentes
- Concluídas

Os filtros consideram:
- tarefa principal
- subtarefas
- contagem correta
- exibição coerente da hierarquia

---

## 🏗️ Arquitetura

Estrutura principal:

```text
modules/
└── todo/
    ├── todo-api
    ├── todo-service
    ├── todo-portlet
    └── todo-test
```

### Responsabilidades dos módulos

#### `todo-api`
Contém contratos e interfaces compartilhadas entre os módulos, incluindo modelos e services públicos.

#### `todo-service`
Responsável por:
- regras de negócio
- validações
- persistência
- controle de permissões por usuário

#### `todo-portlet`
Responsável por:
- camada web
- JSPs
- MVCActionCommands
- MVCRenderCommands
- services auxiliares de upload e feedback da UI

---

## 🧠 Principais decisões técnicas

### 1. Separação de responsabilidades
Os `MVCActionCommand` foram refatorados para evitar concentração excessiva de responsabilidades.

Foram criados serviços auxiliares como:

- `TaskImageUploadService`
- `TaskActionFeedbackService`

Isso tornou o código:
- mais limpo
- mais reutilizável
- mais fácil de testar com Mockito

### 2. Upload de imagem com nome único
Durante os testes manuais, foi encontrado um problema real:

```text
DuplicateFileEntryException
```

Isso acontecia ao tentar subir uma nova imagem com o mesmo nome de uma já existente.

A solução adotada foi gerar um nome único com UUID antes de salvar na Document Library.

### 3. Filtros com suporte a subtarefas
A UI e o render backend foram ajustados para garantir que:
- as abas contem subtarefas corretamente
- a lista exibida reflita a hierarquia
- tarefas pai apareçam quando uma subtarefa combina com o filtro

### 4. Testabilidade
Os fluxos principais foram cobertos com testes unitários para demonstrar:
- domínio de JUnit
- uso de Mockito
- isolamento de dependências
- validação de cenários felizes e de erro

---

## 🔧 Componentes principais

### MVCActionCommands
- `AddTaskMVCActionCommand`
- `UpdateTaskMVCActionCommand`

### MVCRenderCommands
- `TaskViewRenderCommand`
- `EditTaskRenderCommand`

### Services auxiliares
- `TaskImageUploadService`
- `TaskActionFeedbackService`

### Service de negócio
- `TaskLocalService`

---

## 🧪 Testes implementados

Foram implementados testes unitários para:

### `todo-service`
- regras de negócio do `TaskLocalService`
- validações
- permissões
- criação de tarefas e subtarefas
- update
- delete
- toggle de status

### `todo-portlet`
- `AddTaskMVCActionCommand`
- `UpdateTaskMVCActionCommand`

Ferramentas:
- JUnit 4
- Mockito

---

## 🛠️ Tecnologias utilizadas

- Java
- Liferay 7.x
- OSGi
- Gradle
- JSP
- Document Library
- JUnit 4
- Mockito

---

## 📥 Como baixar o projeto

```bash
git clone https://github.com/kirz4/desafio-sea-liferay-todo.git
cd desafio-sea-liferay-todo
```

---

## ▶️ Como executar

Existem duas formas de testar o projeto:

### Opção A — já existe um bundle Liferay dentro do projeto
Use esta opção se a pasta `bundles/` já estiver disponível no ambiente.

#### 1. Build dos módulos

```bash
./gradlew :modules:todo:todo-api:build :modules:todo:todo-service:build :modules:todo:todo-portlet:build --no-daemon
```

#### 2. Copiar os jars para o Liferay

```bash
cp modules/todo/todo-api/build/libs/todo-api.jar bundles/osgi/modules/
cp modules/todo/todo-service/build/libs/todo-service.jar bundles/osgi/modules/
cp modules/todo-portlet/build/libs/todo-portlet.jar bundles/osgi/modules/
```

#### 3. Subir o servidor

```bash
cd bundles/tomcat/bin
./catalina.sh run
```

---

### Opção B — setup completo do zero, para quem não possui o bundle

Esta é a forma recomendada para qualquer avaliador que queira reproduzir o projeto do zero.

#### Pré-requisitos
- Java 17
- Git
- Gradle Wrapper do projeto (já incluído no repositório)
- Um bundle do **Liferay Community 7.4 GA132** compatível com o projeto

#### 1. Clonar o repositório

```bash
git clone https://github.com/kirz4/desafio-sea-liferay-todo.git
cd desafio-sea-liferay-todo
```

#### 2. Baixar um bundle Liferay 7.4 GA132
Baixe um bundle do Liferay Community Edition compatível com a versão usada no projeto e descompacte na raiz do repositório com o nome:

```text
bundles/
```

Ao final, a estrutura esperada deve ficar parecida com:

```text
desafio-sea-liferay-todo/
├── bundles/
│   ├── osgi/
│   ├── tomcat/
│   ├── portal-ext.properties
│   └── ...
├── modules/
└── ...
```

#### 3. Ajustar o `portal-ext.properties`
Caso o arquivo ainda não exista, crie:

```text
bundles/portal-ext.properties
```

Conteúdo mínimo recomendado para ambiente local:

```properties
elasticsearch7.enabled=false
```

#### 4. Build dos módulos

```bash
./gradlew :modules:todo:todo-api:build :modules:todo:todo-service:build :modules:todo:todo-portlet:build --no-daemon
```

#### 5. Copiar os jars gerados para o Liferay

```bash
cp modules/todo/todo-api/build/libs/todo-api.jar bundles/osgi/modules/
cp modules/todo/todo-service/build/libs/todo-service.jar bundles/osgi/modules/
cp modules/todo-portlet/build/libs/todo-portlet.jar bundles/osgi/modules/
```

#### 6. Limpar caches temporários, se necessário
Se for a primeira execução problemática ou se o boot travar:

```bash
rm -rf bundles/osgi/state
rm -rf bundles/work
rm -rf bundles/tomcat/work
rm -rf bundles/tomcat/temp/*
```

#### 7. Subir o servidor

```bash
cd bundles/tomcat/bin
./catalina.sh run
```

#### 8. Acessar no navegador

```text
http://localhost:8080
```

Se for a primeira execução:
- o Liferay pode levar algum tempo para concluir a inicialização
- a base padrão de desenvolvimento pode ser criada automaticamente
- depois basta adicionar a portlet do projeto na página e validar os fluxos descritos no checklist manual

---

## ⚠️ Observação importante sobre ambiente local

Durante o desenvolvimento, o Elasticsearch sidecar do Liferay causou travamento no boot em ambiente local.

Se isso acontecer no seu ambiente, confira o arquivo:

```text
bundles/portal-ext.properties
```

e adicione:

```properties
elasticsearch7.enabled=false
```

Depois limpe caches temporários:

```bash
rm -rf bundles/osgi/state
rm -rf bundles/work
rm -rf bundles/tomcat/work
rm -rf bundles/tomcat/temp/*
```

Isso foi importante para estabilizar o ambiente de desenvolvimento.

---

## ✅ Como rodar os testes

### Testes do service

```bash
./gradlew :modules:todo:todo-service:test --no-daemon
```

### Testes do portlet

```bash
./gradlew :modules:todo:todo-portlet:test --no-daemon
```

### Todos os testes relevantes

```bash
./gradlew :modules:todo:todo-service:test :modules:todo:todo-portlet:test --no-daemon
```

---

## 📂 Estrutura útil do projeto

```text
modules/todo/todo-api
modules/todo/todo-service
modules/todo-portlet
bundles/
```

---

# 📂 Estrutura completa do projeto

```text
desafio-liferay/
├── bundles/                         # Bundle do Liferay (runtime)
│   ├── osgi/
│   ├── tomcat/
│   └── portal-ext.properties
│
├── modules/
│   └── todo/
│       ├── todo-api/               # Contratos (interfaces, models, exceptions)
│       │   ├── src/main/java/com/desafiosea/todo/
│       │   │   ├── exception/
│       │   │   ├── model/
│       │   │   └── service/
│       │   ├── bnd.bnd
│       │   └── build.gradle
│       │
│       ├── todo-service/           # Lógica de negócio + persistência
│       │   ├── src/main/java/com/desafiosea/todo/
│       │   │   ├── model/impl/
│       │   │   ├── service/
│       │   │   │   ├── base/
│       │   │   │   └── impl/
│       │   │   └── persistence/
│       │   │
│       │   ├── src/main/resources/
│       │   │   ├── META-INF/
│       │   │   │   └── sql/
│       │   │   ├── service.xml
│       │   │   └── service.properties
│       │   │
│       │   ├── src/test/java/
│       │   ├── bnd.bnd
│       │   └── build.gradle
│       │
│       ├── todo-portlet/           # Camada WEB (MVC)
│       │   ├── src/main/java/com/desafiosea/todo/web/
│       │   │   ├── action/
│       │   │   │   ├── AddTaskMVCActionCommand.java
│       │   │   │   ├── UpdateTaskMVCActionCommand.java
│       │   │   │   ├── DeleteTaskMVCActionCommand.java
│       │   │   │   └── ToggleTaskStatusMVCActionCommand.java
│       │   │   │
│       │   │   ├── render/
│       │   │   │   ├── TaskViewRenderCommand.java
│       │   │   │   ├── TaskFormRenderCommand.java
│       │   │   │   └── EditTaskRenderCommand.java
│       │   │   │
│       │   │   ├── service/
│       │   │   │   ├── TaskActionFeedbackService.java
│       │   │   │   ├── TaskImageUploadService.java
│       │   │   │   └── impl/
│       │   │   │
│       │   │   ├── constants/
│       │   │   └── portlet/
│       │   │
│       │   ├── src/main/resources/
│       │   │   ├── META-INF/resources/
│       │   │   │   ├── css/
│       │   │   │   │   └── main.css
│       │   │   │   ├── init.jsp
│       │   │   │   ├── view.jsp
│       │   │   │   └── task_form.jsp
│       │   │   │
│       │   │   └── content/
│       │   │       └── Language.properties
│       │   │
│       │   ├── src/test/java/
│       │   ├── bnd.bnd
│       │   └── build.gradle
│       │
│       └── todo-test/              # Testes de integração
│           ├── src/testIntegration/java/
│           └── build.gradle
│
├── gradle/
├── configs/
├── .gitignore
├── build.gradle
└── README.md
```


## 📈 Melhorias futuras

Caso o projeto fosse evoluído além do escopo atual, os próximos passos mais naturais seriam:

- ampliar cobertura de testes para outros commands
- criar testes de integração
- melhorar documentação de setup completo do Liferay
- adicionar paginação ou ordenação de tarefas
- melhorar gerenciamento do arquivo anterior ao atualizar imagem
- adicionar política de remoção de imagens órfãs
- containerização do ambiente se necessário

---

## 👨‍💻 Autor

**Lucas Rabello**

Projeto desenvolvido como desafio técnico, com evolução posterior focada em qualidade de código, robustez e clareza arquitetural.
`
