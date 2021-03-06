import { Component, OnInit, OnDestroy } from '@angular/core';
import { Volunteering } from 'src/app/models/volunteering';
import { NGO } from 'src/app/models/ngo';
import { VolunteeringService } from 'src/app/services/volunteering.service';
import { Category } from 'src/app/models/category';
import { CategoryService } from 'src/app/services/category.service';
import { Form } from '@angular/forms';
import { global } from '../../services/global';
import { EntityService } from 'src/app/services/entity.service';
import { NgoService } from 'src/app/services/ngo.service';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-create-volunteering',
  templateUrl: './create-volunteering.component.html',
  styleUrls: ['./create-volunteering.component.css'],
  providers: [VolunteeringService, NgoService, EntityService, CategoryService]
})
export class CreateVolunteeringComponent implements OnInit, OnDestroy {

  public volunteering: Volunteering;

  public ngoLogged: NGO;

  public categories;

  public category: any;

  public status: string;

  public afuConfig: any;

  private url: string;

  private token: string;
  public identity;




  constructor(private _volunteeringService: VolunteeringService, private _ngoService: NgoService, private _categoryService: CategoryService, private _entityService: EntityService, private _titleService: Title) {
    this._titleService.setTitle("Publicar voluntariado - VoluntaWeb");
    this.volunteering = new Volunteering(null, null, null, "", null, null, null, "", "", "", null, "");


    this.getCategories();



    this.url = global.url;


    this.token = localStorage.getItem('authorization');

    this.volunteering.image = "false";

    this.afuConfig = {
      uploadAPI: {
        url: this.url + 'volunteerings/image/' + this.volunteering.id,
        headers: {
          "Authorization": 'Basic ' + this.token
        }
      },
      multiple: false,
      formatsAllowed: ".jpg",
      maxSize: '50',
      theme: "attachPin",
      hideProgressBar: false,
      hideResetBtn: true,
      hideSelectBtn: false,
      replaceTexts: {
        selectFileBtn: 'Seleccionar archivo...',
        resetBtn: 'Reset',
        uploadBtn: 'Subir',
        dragNDropBox: 'Drag N Drop',
        attachPinBtn: 'Seleccionar archivo...',
        afterUploadMsg_success: '¡Subida satisfactoria!',
        afterUploadMsg_error: '¡Subida fallida!'
      }
    };
  }



  ngOnInit() {
    this.identity = this._entityService.getIdentity();
    this.ngoLogged = this._volunteeringService.getNgoLogged();
  }

  getCategories(): void {

    this._categoryService.getCategories().subscribe(
      (response: any) => {
        if (response) {
          this.categories = response;
          this.category = this.categories[0].id;
        }
        else {
          this.status = 'error';
        }
      },
      error => {
        this.status = 'error';
        console.log(<any>error);
      }
    );
  }

  onSubmit(form) {

    this.volunteering.category = this.categories[this.category - 1];
    this.volunteering.ong = this.ngoLogged;
    this.volunteering.id = null; //The API give the id

    this._volunteeringService.create(this.volunteering).subscribe(
      (response: any) => {
        if (response) {
          this.volunteering = response;
          this.afuConfig.uploadAPI.url = this.url + 'volunteerings/image/' + this.volunteering.id;
        }
        else {
          this.status = 'error';
        }
      },
      error => {
        this.status = 'error';
        console.log(<any>error);
      }
    );



  }



  avatarUpload(data) {

    console.log(this.volunteering);

    console.log(this.afuConfig.uploadAPI.url);

    console.log(data);
    let data_obj = JSON.parse(data.response);
    this.volunteering.image = data_obj.image;
  }

  onImageSubmit() {
    this._volunteeringService.updateVolunteering(this.volunteering.id, this.volunteering).subscribe(
      (response: any) => {
        if (response) {
          this.volunteering = response;
          this.status = "success";
        }
        else {
          this.status = 'error';
        }
      },
      error => {
        console.log(<any>error);
        this.status = 'error';
      }
    );
  }



  //We have to made this instead adding directly to localStorage because there is an error when we add to the set the volunteering that we don´t know how to resolve it.
  //So we have to get the ngo from spring with the new volunteering and bring it here to save it on the localStorage.

  ngOnDestroy() {
    this._ngoService.getNgo(this.ngoLogged.id).subscribe(
      (response: any) => {
        if (response) {
          this.ngoLogged = response;

          localStorage.setItem('identity', JSON.stringify(this.ngoLogged));
        }
        else {
          this.status = 'error';
        }
      },
      error => {
        this.status = 'error';
        console.log(<any>error);
      }
    );
  }
}
