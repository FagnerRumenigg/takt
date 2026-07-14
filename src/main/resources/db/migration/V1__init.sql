create table profiles (
    id uuid primary key,
    area_of_actuation varchar(120) not null,
    role varchar(120) not null,
    job_level varchar(120) not null
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
    created_at timestamptz not null
);

create index idx_refresh_tokens_user_id on refresh_tokens(user_id);
create index idx_users_profile_id on users(profile_id);

create table password_reset_tokens (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    token_hash varchar(128) not null unique,
    expires_at timestamptz not null,
    used boolean not null default false,
    created_at timestamptz not null
);

create index idx_password_reset_tokens_user_id on password_reset_tokens(user_id);

create table email_confirmation_tokens (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    token_hash varchar(128) not null unique,
    expires_at timestamptz not null,
    used boolean not null default false,
    created_at timestamptz not null
);

create index idx_email_confirmation_tokens_user_id on email_confirmation_tokens(user_id);
