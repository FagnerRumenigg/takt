create table profiles (
    id uuid primary key,
    area_of_actuation varchar(120) not null,
    role varchar(120) not null,
    job_level varchar(120) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table users (
    id uuid primary key,
    username varchar(80) not null unique,
    email varchar(160) not null unique,
    full_name varchar(160) null,
    birth_date date null,
    password varchar(255) not null,
    email_verified boolean not null default false,
    profile_id uuid null references profiles(id),
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create table refresh_tokens (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    token_hash varchar(128) not null unique,
    expires_at timestamptz not null,
    revoked boolean not null default false,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index idx_refresh_tokens_user_id on refresh_tokens(user_id);
create index idx_users_profile_id on users(profile_id);

create table password_reset_tokens (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    token_hash varchar(128) not null unique,
    expires_at timestamptz not null,
    used boolean not null default false,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index idx_password_reset_tokens_user_id on password_reset_tokens(user_id);

create table email_confirmation_tokens (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    token_hash varchar(128) not null unique,
    expires_at timestamptz not null,
    used boolean not null default false,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index idx_email_confirmation_tokens_user_id on email_confirmation_tokens(user_id);

create table categories (
    id uuid primary key,
    name varchar(50) not null,
    color varchar(50) null,
    user_id uuid null references users(id) on delete cascade,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index idx_categories_user_id on categories(user_id);

insert into categories (id, name, color, user_id, created_at, updated_at) values
('11111111-1111-1111-1111-111111111111', 'Pessoal', '#4CAF50', null, now(), now()),
('22222222-2222-2222-2222-222222222222', 'Trabalho', '#2196F3', null, now(), now()),
('33333333-3333-3333-3333-333333333333', 'Estudos', '#FF9800', null, now(), now()),
('44444444-4444-4444-4444-444444444444', 'Saúde', '#E91E63', null, now(), now()),
('55555555-5555-5555-5555-555555555555', 'Finanças', '#9C27B0', null, now(), now()),
('66666666-6666-6666-6666-666666666666', 'Casa', '#795548', null, now(), now()),
('77777777-7777-7777-7777-777777777777', 'Projetos', '#009688', null, now(), now()),
('88888888-8888-8888-8888-888888888888', 'Leitura', '#3F51B5', null, now(), now()),
('99999999-9999-9999-9999-999999999999', 'Lazer', '#FF5722', null, now(), now()),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', 'Compras', '#607D8B', null, now(), now())
on conflict do nothing;

create table productivity_levels (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    display_order int not null,
    name varchar(80) not null,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    unique (user_id, display_order)
);

create index idx_productivity_levels_user_id on productivity_levels(user_id);
