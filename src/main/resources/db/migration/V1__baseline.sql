CREATE TABLE payables (
    id uuid not null,
    amount numeric(15,2) not null,
    category varchar(255),
    created_at timestamp not null,
    description varchar(255) not null,
    due_date date not null,
    paid_at timestamp,
    status varchar(20) not null,
    updated_at timestamp,
    vendor varchar(255) not null,
    primary key (id)
);

CREATE TABLE receivables (
    id uuid not null,
    amount numeric(15,2) not null,
    category varchar(255),
    created_at timestamp not null,
    customer varchar(255) not null,
    description varchar(255) not null,
    due_date date not null,
    received_at timestamp,
    status varchar(20) not null,
    updated_at timestamp,
    primary key (id)
);