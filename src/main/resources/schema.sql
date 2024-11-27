-- Spring Boot automatycznie wykoma 'schema.sql' aby utworzyć bazę
CREATE TABLE if not exists Taco_Order (
    id integer AUTO_INCREMENT PRIMARY KEY not null,
    delivery_Name varchar(50) not null,
    delivery_Street varchar(50) not null,
    delivery_City varchar(50) not null,
    delivery_State varchar(2) not null,
    delivery_Zip varchar(10) not null,
    cc_number varchar(16) not null,
    cc_expiration varchar(5) not null,
    cc_cvv varchar(3) not null,
    placed_at TIMESTAMP not null
);

CREATE TABLE if not exists Taco (
    id INTEGER AUTO_INCREMENT PRIMARY KEY not null,
    name varchar(50) not null,
    taco_order INTEGER not null,
    number_in_order INTEGER not null,
    created_at TIMESTAMP not null
);

CREATE TABLE if not exists Ingredient (
    id VARCHAR(4) PRIMARY KEY not null,
    name VARCHAR(25) not null,
    type VARCHAR(10) not null
);

CREATE TABLE if not exists Ingredient_Ref (
    taco_id INTEGER not null,
    ingredient VARCHAR(4) not null,
    pos_in_taco INTEGER not null
);

-- dodajemy klucz obcy, tzn. Zapewniamy że pole 'taco_order' w tabeli Taco będzie mogło mieć
-- tylko i wyłącznie TAKĄ SAMĄ WARTOŚĆ jak pole 'id' z tabeli Taco_order
-- niejako ZMIENIAMY strukturę tabeli
-- klucz obcy (FOREIGN KEY) jest to kolumna w jednej tabeli która wskazuje na klucz podstawowy (PRIMARY KEY) w innej tabeli
--      NIE BĘDZIE MOŻNA USUNĄĆ REKORDU KTÓRY JEST POWIĄZANY Z INNYMI DANYMI (tak jak w tym przypadku)
ALTER TABLE Taco ADD FOREIGN KEY (taco_order) REFERENCES Taco_Order(id);

ALTER TABLE Ingredient_Ref ADD FOREIGN KEY (taco_id) REFERENCES Taco(id);
ALTER TABLE Ingredient_Ref ADD FOREIGN KEY (ingredient) REFERENCES Ingredient(id);