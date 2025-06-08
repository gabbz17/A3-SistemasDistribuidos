const express = require('express');
const path = require('path');
const session = require('express-session');
const fetch = require('node-fetch');

const app = express();
const PORT = process.env.PORT || 3000;

const BACKEND_API_URL = 'http://localhost:8080';

app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

app.use(express.urlencoded({ extended: true }));
app.use(express.json());

app.use(session({
    secret: process.env.SESSION_SECRET || 'sua_chave_secreta_aqui_muito_longa_e_aleatoria_e_segura',
    resave: false,
    saveUninitialized: false,
    cookie: {
        maxAge: 3600000,
        secure: process.env.NODE_ENV === 'production',
        httpOnly: true
    }
}));

async function isAuthenticated(req, res, next) {
    if (!req.session.token) {
        return res.redirect('/login?message=' + encodeURIComponent('Sua sessão expirou ou não está autenticado.'));
    }

    if (req.session.userId && req.session.username && req.session.userProfile !== undefined && req.session.forcePasswordChange !== undefined) {
        req.user = {
            id: req.session.userId,
            name: req.session.username,
            profile: req.session.userProfile,
            token: req.session.token,
            forcePasswordChange: req.session.forcePasswordChange
        };
        if (req.user.forcePasswordChange && req.path !== '/trocar-senha') {
            return res.redirect('/trocar-senha');
        }
        return next();
    }
    try {
        const userInfoResponse = await fetch(`${BACKEND_API_URL}/api/funcionario`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${req.session.token}`
            }
        });

        if (userInfoResponse.ok) {
            const userInfo = await userInfoResponse.json();
            req.session.userId = userInfo.id;
            req.session.username = userInfo.nome;
            req.session.userProfile = userInfo.cargo;
            req.session.forcePasswordChange = userInfo.primeiroLogin;

            req.user = {
                id: req.session.userId,
                name: userInfo.nome,
                profile: userInfo.cargo,
                token: req.session.token,
                forcePasswordChange: userInfo.primeiroLogin
            };
            return next();
        } else if (userInfoResponse.status === 401) {
            console.warn('Token JWT inválido/expirado ao buscar info do usuário. Redirecionando para login.');
            req.session.destroy(err => { if (err) console.error(err); });
            return res.redirect('/login?message=' + encodeURIComponent('Sua sessão expirou. Faça login novamente.'));
        } else {
            console.error(`Erro ao buscar informações do usuário do backend: ${userInfoResponse.status} - ${await userInfoResponse.text()}`);
            req.session.destroy(err => { if (err) console.error(err); });
            return res.redirect('/login?message=' + encodeURIComponent('Não foi possível obter os dados do seu perfil. Faça login novamente.'));
        }
    } catch (error) {
        console.error('Erro de rede ao buscar informações do usuário:', error);
        req.session.destroy(err => { if (err) console.error(err); });
        return res.redirect('/login?message=' + encodeURIComponent('Erro de conexão ao verificar seu perfil.'));
    }
}

app.use(express.static(path.join(__dirname, 'public')));

app.get('/', isAuthenticated, async (req, res) => {
    const user = req.user;

    if (user.forcePasswordChange) {
        return res.redirect('/trocar-senha');
    }

    let menuOptions = [];
    let pageData = {
        tarefas: [],
        usuarios: []
    };

    switch (user.profile) {
        case 'GERENTE':
            menuOptions = [
                { name: 'Tarefas', url: '#tarefas' },
                { name: 'Criar Usuário', url: '#criar-usuario' }
            ];
            try {
                const tarefasResponseGerente = await fetch(`${BACKEND_API_URL}/api/tarefa`, {
                    headers: { 'Authorization': `Bearer ${req.session.token}` }
                });
                if (tarefasResponseGerente.ok) {
                    pageData.tarefas = await tarefasResponseGerente.json();
                } else {
                    console.warn(`Erro ao buscar tarefas (Gerente): ${tarefasResponseGerente.status}`);
                    pageData.tarefas = [{ titulo: 'Erro ao carregar tarefas (Gerente).', status: 'Falha' }];
                }

                const usuariosResponseGerente = await fetch(`${BACKEND_API_URL}/api/funcionario/all`, {
                    headers: { 'Authorization': `Bearer ${req.session.token}` }
                });
                if (usuariosResponseGerente.ok) {
                    pageData.usuarios = await usuariosResponseGerente.json();
                } else {
                    const errorText = await usuariosResponseGerente.text();
                    console.warn(`Erro ao buscar USUÁRIOS (Gerente): ${usuariosResponseGerente.status} - ${errorText}`);
                    pageData.usuarios = [];
                }
            } catch (error) {
                console.error('Erro de rede ao buscar dados (GERENTE):', error);
                pageData.tarefas = [{ titulo: 'Erro de conexão ao carregar tarefas (Gerente).', status: 'Falha' }];
                pageData.usuarios = [];
            }
            break;

        case 'SUPERVISOR':
            menuOptions = [
                { name: 'Tarefas', url: '#tarefas' },
                { name: 'Criar Tarefas', url: '#criar-tarefas' }
            ];
            try {
                const tarefasResponseSupervisor = await fetch(`${BACKEND_API_URL}/api/tarefa`, {
                    headers: { 'Authorization': `Bearer ${req.session.token}` }
                });
                if (tarefasResponseSupervisor.ok) {
                    pageData.tarefas = await tarefasResponseSupervisor.json();
                } else {
                    console.warn(`Erro ao buscar tarefas (Supervisor): ${tarefasResponseSupervisor.status}`);
                    pageData.tarefas = [{ titulo: 'Erro ao carregar tarefas (Supervisor).', status: 'Falha' }];
                }

                const usuariosResponseSupervisor = await fetch(`${BACKEND_API_URL}/api/funcionario/all`, {
                    headers: { 'Authorization': `Bearer ${req.session.token}` }
                });
                if (usuariosResponseSupervisor.ok) {
                    pageData.usuarios = await usuariosResponseSupervisor.json();
                } else {
                    const errorText = await usuariosResponseSupervisor.text();
                    console.warn(`Erro ao buscar USUÁRIOS (Supervisor): ${usuariosResponseSupervisor.status} - ${errorText}`);
                    pageData.usuarios = [];
                }
            } catch (error) {
                console.error('Erro de rede ao buscar tarefas (SUPERVISOR):', error);
                pageData.tarefas = [{ titulo: 'Erro de conexão ao carregar tarefas (Supervisor).', status: 'Falha' }];
                pageData.usuarios = [];
            }
            break;

        case 'ATENDENTE':
            menuOptions = [
                { name: 'Minhas Tarefas', url: '#tarefas' }
            ];
            try {
                const tarefasResponse = await fetch(`${BACKEND_API_URL}/api/tarefa/minhas-tarefas`, {
                    headers: { 'Authorization': `Bearer ${req.session.token}` }
                });
                if (tarefasResponse.ok) {
                    pageData.tarefas = await tarefasResponse.json();
                } else if (tarefasResponse.status === 404) {
                    pageData.tarefas = [];
                } else {
                    console.warn(`Erro ao buscar tarefas para ATENDENTE: ${tarefasResponse.status}`);
                    pageData.tarefas = [{ titulo: 'Erro ao carregar tarefas (Atendente).', status: 'Falha' }];
                }
                pageData.usuarios = [];
            } catch (error) {
                console.error('Erro de rede ao buscar tarefas (ATENDENTE):', error);
                pageData.tarefas = [{ titulo: 'Erro de conexão ao carregar tarefas (Atendente).', status: 'Falha' }];
            }
            break;

        default:
            console.log('Perfil desconhecido ou inválido:', user.profile); // Este é um log informativo que pode ser mantido ou removido
            req.session.destroy(err => { if (err) console.error(err); });
            return res.redirect('/login?error=' + encodeURIComponent('Perfil de usuário desconhecido ou inválido.'));
    }

    res.render('layout', {
        title: 'Home',
        user: user,
        profile: user.profile,
        menuOptions: menuOptions,
        pageData: pageData,
        errorMessage: req.query.error || '',
        successMessage: req.query.message || '',
        backendApiUrl: BACKEND_API_URL
    });
});


app.get('/login', (req, res) => {
    res.render('login', {
        title: 'Login',
        message: req.query.message || '',
        error: req.query.error || ''
    });
});

app.post('/login', async (req, res) => {
    const { email, senha } = req.body;

    try {
        const response = await fetch(`${BACKEND_API_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, senha })
        });

        let data;
        try {
            data = JSON.parse(await response.text());
        } catch (jsonError) {
            console.error('Erro ao parsear JSON da resposta do backend:', jsonError);
            return res.render('login', {
                title: 'Login',
                message: '',
                error: `Resposta inválida do backend: ${responseText.substring(0, 200)}...`
            });
        }

        if (response.ok) {
            if (data.token && data.userId && data.username && data.profile) {
                req.session.token = data.token;
                req.session.userId = data.userId;
                req.session.username = data.username;
                req.session.userProfile = data.profile;
                req.session.forcePasswordChange = data.forcePasswordChange || false;

                if (req.session.forcePasswordChange) {
                    return res.redirect('/trocar-senha');
                } else {
                    return res.redirect('/');
                }
            } else {
                const errorMessage = data.message || 'Resposta de login incompleta do servidor. (Faltam token, userId, username ou profile).';
                console.error('Login bem-sucedido pelo status, mas dados incompletos:', data);
                return res.render('login', { title: 'Login', message: '', error: errorMessage });
            }
        } else {
            const errorMessage = data.message || 'Credenciais inválidas ou erro no servidor. Tente novamente.';
            console.error('Login falhou com status:', response.status, 'Mensagem:', errorMessage);
            return res.render('login', { title: 'Login', message: '', error: errorMessage });
        }
    } catch (error) {
        console.error('Erro de rede/conexão na requisição de login:', error);
        return res.render('login', {
            title: 'Login',
            message: '',
            error: 'Não foi possível conectar ao servidor de autenticação. (Verifique sua conexão ou o status do backend).'
        });
    }
});


app.get('/trocar-senha', isAuthenticated, (req, res) => {
    const user = req.user;
    if (!user.forcePasswordChange) {
        return res.redirect('/');
    }
    res.render('trocar_senha', {
        title: 'Trocar Senha',
        user: user,
        errorMessage: req.query.error || '',
        successMessage: req.query.message || ''
    });
});


app.get('/logout', (req, res) => {
    req.session.destroy(err => {
        if (err) {
            console.error('Erro ao encerrar a sessão:', err);
            return res.redirect('/');
        }
        res.clearCookie('connect.sid');
        return res.redirect('/login?message=' + encodeURIComponent('Você foi desconectado com sucesso.'));
    });
});

app.post('/trocar-senha', isAuthenticated, async (req, res) => {
    const { senhaAntiga, novaSenha } = req.body;
    const userId = req.user.id;
    const token = req.user.token;

    try {
        const response = await fetch(`${BACKEND_API_URL}/api/funcionario/trocar-senha`, {
            method: 'PATCH',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ senhaAntiga, novaSenha })
        });

        if (response.ok) {
            req.session.forcePasswordChange = false;
            return res.redirect('/trocar-senha?message=' + encodeURIComponent('Senha alterada com sucesso! Você já pode acessar o sistema.'));
        } else {
            const errorData = await response.json();
            const errorMessage = errorData.message || 'Erro ao trocar a senha.';
            return res.redirect('/trocar-senha?error=' + encodeURIComponent(errorMessage));
        }
    } catch (error) {
        console.error('Erro na requisição de troca de senha:', error);
        return res.redirect('/trocar-senha?error=' + encodeURIComponent('Erro de conexão ao trocar a senha.'));
    }
});


app.listen(PORT, () => {
    console.log(`Servidor de Frontend rodando em http://localhost:${PORT}`);
    console.log(`Configurado para conectar ao Backend em: ${BACKEND_API_URL}`);
});