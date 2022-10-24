package com.example.lab3_1;

public enum gameTypes{
    Flags(new ParserParams("https://www.dorogavrim.ru/articles/flagi_stran_mira/",
            "https://www.dorogavrim.ru",
            "<table style=\"width: 100%;\" align=\"center\">",
            "</table>",
            " src=\"(.*?)\" style=",
            "<br>\t\t (.*?)</")
    ),
    Сelebrities(new ParserParams(
            "https://www.theplace.ru/photos/",
            "https://www.theplace.ru",
            "<div class=\"models_list row\">",
            "<div class=\"col-md-3 col-sm-4 main-col-right\">",
            " <span class=\"ico_box\">                                <img src=\"(.*?)\" class=\"icon\"",
            "<span class=\"name\">(.*?)</span>")

    ),
    СatBreeds(new ParserParams(
            "https://pets.mail.ru/cat-breeds/",
            "",
            "<div class=\"pets-kinds-list\" data-display-view=\"compact\">",
            "<span class=\"hdr__inner\">Подборки пород</span>",
            "<img class=\"pets-kind-item__image\" src=\"(.*?)\" alt=",
            "alt=\"(.*?)\"/></div><div class=\"pets-kind-item__content\">")),

    DogBreeds(new ParserParams(
            "https://pets.mail.ru/dog-breeds/",
            "",
            "<div class=\"pets-kinds-list\" data-display-view=\"compact\">",
            "<span class=\"hdr__inner\">Подборки пород</span>",
            "<img class=\"pets-kind-item__image\" src=\"(.*?)\" alt=",
            "alt=\"(.*?)\"/></div><div class=\"pets-kind-item__content\">"));

    private ParserParams value;

    private gameTypes(ParserParams value) {
        this.value = value;
    }


    public ParserParams getValue(){
        return value;
    }

}
