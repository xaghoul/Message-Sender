package com.xaghoul.demo.service.impl;

import com.xaghoul.demo.exception.MessageTemplateNotFoundException;
import com.xaghoul.demo.model.MessageTemplate;
import com.xaghoul.demo.repository.MessageTemplateRepository;
import com.xaghoul.demo.service.TemplateMessageService;
import com.xaghoul.demo.web.controller.MessageTemplateController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class TemplateMessageServiceImpl implements TemplateMessageService {

    private final MessageTemplateRepository repository;
    private final MessageTemplateModelAssembler assembler;

    @Autowired
    public TemplateMessageServiceImpl(MessageTemplateRepository repository, MessageTemplateModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @Override
    public CollectionModel<EntityModel<MessageTemplate>> getAll() {
        List<EntityModel<MessageTemplate>> templates = repository.findAll()
                .stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(templates,
                linkTo(methodOn(MessageTemplateController.class).getAllTemplates()).withSelfRel());
    }

    @Override
    public EntityModel<MessageTemplate> getById(UUID id) {
        MessageTemplate messageTemplate = repository.findById(id)
                .orElseThrow(() -> new MessageTemplateNotFoundException(id));

        return assembler.toModel(messageTemplate);
    }

    @Override
    public ResponseEntity<?> postTemplate(MessageTemplate newMessageTemplate) {
        EntityModel<MessageTemplate> messageTemplateModel = assembler.toModel(repository.save(newMessageTemplate));

        return ResponseEntity
                .created(messageTemplateModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(messageTemplateModel);
    }

    @Override
    public ResponseEntity<?> putTemplate(MessageTemplate newMessageTemplate, UUID id) {
        MessageTemplate updatedMessageTemplate = repository.findById(id)
                .map(messageTemplate -> {
                    messageTemplate.setName(newMessageTemplate.getName());
                    messageTemplate.setTemplate(newMessageTemplate.getTemplate());
                    messageTemplate.setRecipients(newMessageTemplate.getRecipients());
                    return repository.save(messageTemplate);
                })
                .orElseGet(() -> {
                    newMessageTemplate.setId(id);
                    return repository.save(newMessageTemplate);
                });

        EntityModel<MessageTemplate> messageTemplateModel = assembler.toModel(updatedMessageTemplate);

        return ResponseEntity
                .created(messageTemplateModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(messageTemplateModel);
    }

    @Override
    public ResponseEntity<?> deleteTemplate(UUID id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @Override
    public MessageTemplate getByName(String name) {
        if(repository.getByName(name).isPresent())
            return repository.getByName(name).get();
        else
            throw new MessageTemplateNotFoundException(name);
    }
}
