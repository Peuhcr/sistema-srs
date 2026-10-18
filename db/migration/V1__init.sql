
CREATE TABLE organizacoes (

    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nome TEXT NOT NULL,
    descricao TEXT,
    criada_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizada_em TIMESTAMPTZ

);

CREATE TABLE usuarios (


    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nome TEXT NOT NULL,
    matricula TEXT UNIQUE NOT NULL,
    email TEXT UNIQUE NOT NULL,
    tipo TEXT NOT NULL,
    organizacoes_id BIGINT REFERENCES organizacoes(id),
    senha_hash VARCHAR(255) NOT NULL,
    criada_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizada_em TIMESTAMPTZ,

    CONSTRAINT chk_usuarios_tipo CHECK (tipo IN ('admin', 'membro')),
    CONSTRAINT chk_usuarios_org CHECK (

        (tipo = 'admin' AND organizacoes_id IS NULL) OR
        (tipo = 'membro' AND organizacoes_id IS NOT NULL)
    )

);


CREATE TABLE salas (

    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    nome TEXT NOT NULL,
    bloco TEXT NOT NULL,
    tipo TEXT NOT NULL, 
    capacidade INTEGER NOT NULL,
    criada_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizada_em TIMESTAMPTZ,

    CONSTRAINT chk_salas_tipo CHECK (tipo IN ('laboratorio', 
                                            'sala_padrao',
                                            'sala_geminada'))
);


CREATE TABLE reservas (

    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    salas_id BIGINT REFERENCES salas(id) NOT NULL,
    responsavel_id BIGINT REFERENCES usuarios(id) NOT NULL,
    organizacoes_id BIGINT REFERENCES organizacoes(id) NOT NULL,
    data_inicio TIMESTAMPTZ NOT NULL,
    data_fim TIMESTAMPTZ NOT NULL,
    status TEXT NOT NULL,
    titulo TEXT NOT NULL,
    descricao TEXT,
    aprovador_id BIGINT REFERENCES usuarios(id),
    num_participantes INTEGER NOT NULL,
    criada_em TIMESTAMPTZ NOT NULL DEFAULT now(),
    atualizada_em TIMESTAMPTZ,

    CONSTRAINT chk_reserva_status CHECK ( status IN ('pendente', 
                                                    'aprovada', 
                                                    'recusada')),

    CONSTRAINT chk_reserva_datas CHECK (data_inicio < data_fim)
);

CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE reservas ADD CONSTRAINT exclui_reserva_conflito EXCLUDE USING gist (

    salas_id WITH =,
    tstzrange(data_inicio, data_fim, '[)') WITH &&) WHERE (status IN ('pendente', 'aprovada'));

    /* tstzrange (Time Stamp Time Zone Range, esse trecho transforma os atributuos do tipo TIMESTAMPTZ
        correspondentes à data de início e fim das reservas concorrentes e as transforma em um 
        Time Range Object. 
    */






